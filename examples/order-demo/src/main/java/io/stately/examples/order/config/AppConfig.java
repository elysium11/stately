package io.stately.examples.order.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import io.stately.core.LockingStrategy;
import io.stately.core.StateGraph;
import io.stately.core.TransitionManager;
import io.stately.core.store.FsmTransactionManager;
import io.stately.core.store.OutboxAppender;
import io.stately.core.store.OutboxProcessor;
import io.stately.core.store.TransitionLogStore;
import io.stately.examples.order.domain.Order;
import io.stately.examples.order.domain.OrderRepository;
import io.stately.examples.order.fsm.OrderEvent;
import io.stately.examples.order.fsm.OrderState;
import io.stately.examples.order.fsm.persistence.FsmTransitionRepository;
import io.stately.examples.order.fsm.persistence.OutboxRepository;
import io.stately.examples.order.service.JdbcOrderAggregateStore;
import io.stately.examples.order.service.JdbcOutboxAppender;
import io.stately.examples.order.service.JdbcTransitionLogStore;
import io.stately.examples.order.service.OutboxDispatcher;
import io.stately.examples.order.service.OutboxRecoveryJob;
import io.stately.examples.order.sync.OperationWaiter;
import io.stately.spring.SpringTransactionManager;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@EnableScheduling
public class AppConfig {

  @Bean
  public JdbcOrderAggregateStore orderStore(OrderRepository repo) {
    return new JdbcOrderAggregateStore(repo);
  }

  @Bean
  public TransitionLogStore<OrderState, OrderEvent> transitionLog(FsmTransitionRepository r) {
    return new JdbcTransitionLogStore<>(r);
  }

  @Bean
  public OutboxAppender outboxAppender(OutboxRepository outboxRepository, ObjectMapper om) {
    return new JdbcOutboxAppender(outboxRepository, om);
  }

  @Bean
  public OperationWaiter operationWaiter() {
    return new OperationWaiter();
  }

  @Bean
  public OutboxProcessor outboxDispatcher(
      OutboxRepository repo,
      ObjectMapper om,
      OperationWaiter waiter
  ) {
    return new OutboxDispatcher(repo, om, waiter);
  }

  @Bean
  public OutboxRecoveryJob dispatcher(
      OutboxRepository repo,
      OutboxProcessor outboxDispatcher
  ) {
    return new OutboxRecoveryJob(repo, outboxDispatcher);
  }

  @Bean
  public FsmTransactionManager fsmTransactionManager(TransactionTemplate template) {
    return new SpringTransactionManager(template);
  }

  @Bean
  public TransitionManager<Order, OrderState, OrderEvent, UUID> tm(
      StateGraph<OrderState, OrderEvent> graph,
      JdbcOrderAggregateStore store,
      TransitionLogStore<OrderState, OrderEvent> log,
      OutboxAppender outbox,
      OutboxProcessor outboxDispatcher,
      LockingStrategy<UUID> lock,
      FsmTransactionManager fsmTransactionManager
  ) {
    return new TransitionManager<>("Order", graph, store, log, outbox, outboxDispatcher, lock, fsmTransactionManager);
  }

  @Bean
  public LockingStrategy<UUID> lockingStrategy(OrderRepository repo) {
    return new LockingStrategy<>() {
      @Override
      public void lock(String aggregateType, UUID aggregateId) { }

      @Override
      public void unlock(String aggregateType, UUID aggregateId) { }
    };
  }

  @Bean
  public GuavaModule guavaModule() {
    return new GuavaModule();
  }
}
