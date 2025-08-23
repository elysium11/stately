package io.stately.examples.order.service;

import io.stately.core.store.AggregateStateStore;
import io.stately.examples.order.domain.Order;
import io.stately.examples.order.domain.OrderRepository;
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
    return a.state();
  }

  @Override
  public Order setState(Order a, OrderState newState) {
    return a.withState(newState);
  }

  @Override
  public void save(Order a) {
    repo.save(a);
  }
}
