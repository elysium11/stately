package io.stately.examples.order.outbox;

import io.stately.core.store.OutboxAppender.Payload;
import java.util.Map;
import java.util.UUID;

/**
 * Типизированный payload для события авторизации платежа.
 */
public record PaymentAuthorizationPayload(
    String orderId,
    Integer amount,
    String currency
) implements Payload {

  public static PaymentAuthorizationPayload of(UUID orderId, Integer amount, String currency) {
    return new PaymentAuthorizationPayload(orderId.toString(), amount, currency);
  }

  /**
   * Конвертирует payload в Map для сериализации в outbox.
   */
  public Map<String, Object> toMap() {
    return Map.of(
        "orderId", orderId,
        "amount", amount,
        "currency", currency
    );
  }
}
