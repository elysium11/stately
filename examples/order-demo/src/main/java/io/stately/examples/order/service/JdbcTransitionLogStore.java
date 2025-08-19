package io.stately.examples.order.service;

import com.google.common.collect.ImmutableMap;
import io.stately.core.store.TransitionLogStore;
import io.stately.examples.order.fsm.persistence.FsmTransitionEntity;
import io.stately.examples.order.fsm.persistence.FsmTransitionRepository;
import java.time.Instant;
import java.util.Map;

public class JdbcTransitionLogStore<S extends Enum<S>, E extends Enum<E>>
    implements TransitionLogStore<S, E> {

  private final FsmTransitionRepository repo;

  public JdbcTransitionLogStore(FsmTransitionRepository repo) {
    this.repo = repo;
  }

  @Override
  public void record(
      String aggregateType,
      Object aggregateId,
      S from,
      S to,
      E event,
      Instant at,
      Map<String, Object> metadata,
      String idempotencyKey
  ) {
    var e = new FsmTransitionEntity(
        null,
        aggregateType,
        String.valueOf(aggregateId),
        from.name(),
        to.name(),
        event.name(),
        at != null ? at : Instant.now(),
        metadata != null ? ImmutableMap.copyOf(metadata) : null,
        idempotencyKey
    );
    repo.save(e);
  }
}