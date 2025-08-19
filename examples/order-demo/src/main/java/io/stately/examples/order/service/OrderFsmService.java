package io.stately.examples.order.service;

import io.stately.core.StateGraph;
import io.stately.core.TransitionManager;
import io.stately.examples.order.domain.Order;
import io.stately.examples.order.domain.OrderRepository;
import io.stately.examples.order.fsm.OrderEvent;
import io.stately.examples.order.fsm.OrderState;
import io.stately.examples.order.outbox.OutboxEventType;
import io.stately.examples.order.outbox.PaymentAuthorizationPayload;
import io.stately.examples.order.sync.OperationWaiter;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OrderFsmService {

  private final OrderRepository repo;
  private final TransitionManager<Order, OrderState, OrderEvent, UUID> tm;
  private final OperationWaiter waiter;
  private final StateGraph<OrderState, OrderEvent> graph;

  public OrderFsmService(
      OrderRepository repo,
      TransitionManager<Order, OrderState, OrderEvent, UUID> tm,
      OperationWaiter waiter,
      StateGraph<OrderState, OrderEvent> graph
  ) {
    this.repo = repo;
    this.tm = tm;
    this.waiter = waiter;
    this.graph = graph;
  }

  public UUID submit(UUID id, String idempotencyKey, Integer orderAmount) {
    // 1) Синхронный запрос — будем ждать operationId
    String operationId = idempotencyKey; // можно сгенерировать отдельно

    // 2) FSM: сменим состояние, залогируем переход и положим команду во внешку в outbox (emit)
    tm.transition(
        id, OrderEvent.SUBMIT, ctx -> {
          // Демонстрируем использование action для бизнес-логики
          ctx.action(order -> {
            // Для immutable record это демонстрация концепции
            // В реальном случае здесь может быть валидация, логирование, etc.
            System.out.println("[DEBUG_LOG] Processing order with amount: " + orderAmount +
                                   ", current order amount: " + order.amount());

            // Примечание: для полноценной работы с immutable records
            // нужно расширить архитектуру для возврата новой версии агрегата
          });

          ctx.metadata(Map.of("idempotencyKey", operationId));
          ctx.emit(
              OutboxEventType.CALL_PAYMENT_PROVIDER_AUTHORIZE,
              PaymentAuthorizationPayload.of(id, orderAmount, "USD"),
              operationId
          );
        }
    );

    // 3) Ждём результат обработки outbox-диспетчером (вариант C)
    try {
      var res = waiter.await(operationId, Duration.ofSeconds(5));
      if (!res.success) {
        // При желании: отдельный компенсационный переход
        throw new IllegalStateException("External failed: " + res.details);
      }
      return id;
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Interrupted", ie);
    } catch (java.util.concurrent.TimeoutException te) {
      throw new IllegalStateException("Timeout waiting external result", te);
    }
  }

  // Примеры других переходов — уже можно без ручных save:
  public void cancel(UUID id) {
    tm.transition(id, OrderEvent.CANCEL, ctx -> { /* guards/meta as needed */ });
  }
}
