package io.stately.examples.order.outbox;

/**
 * Типизированные типы событий для outbox.
 */
public enum OutboxEventType {

  CALL_PAYMENT_PROVIDER_AUTHORIZE("CallPaymentProviderAuthorize");

  private final String eventName;

  OutboxEventType(String eventName) {
    this.eventName = eventName;
  }

  public String getEventName() {
    return eventName;
  }

  @Override
  public String toString() {
    return eventName;
  }
}
