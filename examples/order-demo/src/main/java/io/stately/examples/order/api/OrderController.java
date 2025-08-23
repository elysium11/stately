package io.stately.examples.order.api;

import io.stately.examples.order.domain.Order;
import io.stately.examples.order.domain.OrderRepository;
import io.stately.examples.order.service.OrderFsmService;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

  private final OrderRepository repo;
  private final OrderFsmService fsm;

  public OrderController(OrderRepository repo, OrderFsmService fsm) {
    this.repo = repo;
    this.fsm = fsm;
  }

  @PostMapping
  public UUID create() {
    var o = Order.create();
    repo.save(o);
    return o.id();
  }

  @PostMapping("/{id}/submit")
  public void submit(
      @PathVariable UUID id,
      @RequestHeader(value = "Idempotency-Key", required = false) String key,
      @RequestParam(value = "amount", defaultValue = "100") Integer amount
  ) {
    fsm.submit(id, key, amount);
  }
}
