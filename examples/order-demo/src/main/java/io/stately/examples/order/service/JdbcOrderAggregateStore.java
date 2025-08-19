package io.stately.examples.order.service;

import io.stately.examples.order.domain.Order;
import io.stately.examples.order.domain.OrderRepository;
import io.stately.core.store.AggregateStateStore;
import io.stately.examples.order.fsm.OrderEvent;
import io.stately.examples.order.fsm.OrderState;
import java.util.UUID;

public class JdbcOrderAggregateStore implements AggregateStateStore<Order, OrderState, UUID> {

  private final OrderRepository repo;

  public JdbcOrderAggregateStore(OrderRepository repo) {
    this.repo = repo;
  }

  @Override
  public Order loadForUpdate(UUID id) {
    return repo.lockById(id).orElseThrow();
  }

  @Override
  public OrderState getState(Order a) {
    return OrderState.valueOf(a.state());
  }

  @Override
  public void setState(Order a, OrderState newState) { /* no-op: immutable record */ }

  @Override
  public void save(Order a) {
    repo.save(a);
  }

  public Order mutate(Order a, String newState) {
    return a.withState(newState);
  }
}