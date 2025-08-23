package io.stately.core;

import com.github.f4b6a3.uuid.UuidCreator;
import io.stately.core.store.AggregateStateStore;
import io.stately.core.store.FsmTransactionManager;
import io.stately.core.store.OutboxAppender;
import io.stately.core.store.OutboxEventImpl;
import io.stately.core.store.TransitionLogStore;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class TransitionManager<A, S, E, ID> {

  private final String aggregateType;
  private final StateGraph<S, E> graph;
  private final AggregateStateStore<A, S, ID> store;
  private final TransitionLogStore<S, E> logStore;
  private final OutboxAppender outbox;       // <— новое
  private final LockingStrategy<ID> locking;
  private final FsmTransactionManager fsmTransactionManager;

  public TransitionManager(
      String aggregateType,
      StateGraph<S, E> graph,
      AggregateStateStore<A, S, ID> store,
      TransitionLogStore<S, E> logStore,
      OutboxAppender outbox,
      LockingStrategy<ID> locking,
      FsmTransactionManager fsmTransactionManager
  ) {
    this.aggregateType = Objects.requireNonNull(aggregateType);
    this.graph = Objects.requireNonNull(graph);
    this.store = Objects.requireNonNull(store);
    this.logStore = Objects.requireNonNull(logStore);
    this.outbox = outbox == null ? new OutboxAppender.Noop() : outbox;
    this.locking = Objects.requireNonNull(locking);
    this.fsmTransactionManager = fsmTransactionManager == null ?
        new FsmTransactionManager.Noop() : fsmTransactionManager;
  }

  @FunctionalInterface
  public interface TransitionHandler<A, S, E> {

    void configure(TransitionContext<A> ctx);
  }

  public void transition(ID aggregateId, E event, TransitionHandler<A, S, E> handler) {
    transition(aggregateId, event, null, handler);
  }

  public void transition(
      ID aggregateId,
      E event,
      String idempotencyKey,
      TransitionHandler<A, S, E> handler
  ) {
    Objects.requireNonNull(aggregateId);
    locking.lock(aggregateType, aggregateId);
    try {
      transitionInternal(aggregateId, event, idempotencyKey, handler);
    } finally {
      locking.unlock(aggregateType, aggregateId);
    }
  }

  private void transitionInternal(
      ID aggregateId,
      E event,
      String idempotencyKey,
      TransitionHandler<A, S, E> handler
  ) {
    // Оборачиваем всю логику перехода в транзакцию
    fsmTransactionManager.executeInTransaction(() -> {
      A agg = store.loadForUpdate(aggregateId);
      S from = store.getState(agg);
      S to = graph.nextByEvent(from, event)
          .filter(nt -> graph.canTransition(from, nt))
          .orElseThrow(() -> new IllegalStateException(
              "Transition not allowed: " + from + " -> event " + event));

      var ctx = new TransitionContext<A>();
      handler.configure(ctx);
      if (!ctx.guardsOk()) {
        throw new IllegalStateException("Guard failed");
      }

      // Применяем действия к агрегату перед изменением состояния
      A modifiedAgg = agg;
      for (var action : ctx.actions()) {
        try {
          modifiedAgg = action.apply(modifiedAgg);
        } catch (Exception e) {
          throw new RuntimeException("Action failed during transition", e);
        }
      }

      // Смена состояния агрегата (мутабельный/иммутабельный — решает реализация стора).
      store.save(store.setState(modifiedAgg, to));

      // Логируем сам переход.
      Map<String, Object> meta = ctx.meta();
      logStore.record(aggregateType, aggregateId, from, to, event, Instant.now(), meta, idempotencyKey);

      // Записываем декларации эффектов в outbox (в той же транзакции).
      for (var em : ctx.emissions()) {
        outbox.append(
            new OutboxEventImpl(
                UuidCreator.getTimeOrderedEpoch(),
                aggregateType,
                aggregateId,
                em.type(),
                em.payload(),
                em.operationId()
            )
        );
      }

      return null; // Void operation
    });
  }
}
