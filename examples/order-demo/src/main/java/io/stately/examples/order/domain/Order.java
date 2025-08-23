package io.stately.examples.order.domain;

import io.stately.examples.order.fsm.OrderState;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table("orders")
public record Order(
    @Id
    UUID id,
    @Version
    Integer version,
    OrderState state,
    Instant createdAt,
    Instant updatedAt,
    Integer amount
) {

  public static Order create() {
    var now = Instant.now();
    return new Order(UUID.randomUUID(), null, OrderState.NEW, now, now, 0);
  }

  public Order withState(OrderState newState) {
    return new Order(this.id, this.version, newState, this.createdAt, Instant.now(), this.amount);
  }

  public Order withAmount(Integer newAmount) {
    return new Order(this.id, this.version, this.state, this.createdAt, Instant.now(), newAmount);
  }
}
