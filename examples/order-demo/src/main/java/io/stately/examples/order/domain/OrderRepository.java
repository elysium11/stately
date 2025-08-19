package io.stately.examples.order.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, UUID> {

  @Query("SELECT * FROM orders WHERE id = :id FOR UPDATE")
  Optional<Order> lockById(UUID id);
}