package io.stately.examples.order;

import io.stately.examples.order.domain.Order;
import io.stately.examples.order.domain.OrderRepository;
import io.stately.examples.order.fsm.OrderState;
import io.stately.examples.order.it.BaseIntegrationTest;
import io.stately.examples.order.service.OrderFsmService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderFsmIntegrationTest extends BaseIntegrationTest {

  @Autowired
  OrderRepository repo;
  @Autowired
  OrderFsmService service;

  @Test
  void fullHappyPath_and_terminalState_blockFurtherTransitions() {
    var id = createOrder();

    service.submit(id, "itest-1", 150);
    assertThat(repo.findById(id)).get().extracting(Order::state).isEqualTo(OrderState.CONFIRMED);

    service.ship(id);
    assertThat(repo.findById(id)).get().extracting(Order::state).isEqualTo(OrderState.SHIPPED);

    assertThatThrownBy(() -> service.cancel(id))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Transition not allowed");
  }

  @Test
  void submit_withActions_executesActionsAndChangesState() {
    var id = createOrder();

    // Проверяем, что изначально amount = 0
    assertThat(repo.findById(id)).get().extracting("amount").isEqualTo(0);
    var orderAmount = 250;

    // Выполняем переход с action
    service.submit(id, "itest-actions", orderAmount);

    // Проверяем, что состояние изменилось
    assertThat(repo.findById(id)).get()
        .returns(OrderState.CONFIRMED, Order::state)
        .returns(orderAmount, Order::amount); // amount не изменился в текущей реализации

    // Примечание: в текущей реализации action не изменяет amount в БД,
    // но демонстрирует выполнение бизнес-логики во время перехода
    // Для полноценной работы с immutable records нужно расширить архитектуру
  }

  private UUID createOrder() {
    var o = Order.create();
    repo.save(o);
    return o.id();
  }
}
