package io.stately.core.store;

/**
 * Контракт записи внешних эффектов в outbox (в той же транзакции, что и переход).
 */
public interface OutboxAppender {

  void append(OutboxEvent outboxEvent);

  /**
   * No-op реализация по умолчанию.
   */
  class Noop implements OutboxAppender {

    @Override
    public void append(OutboxEvent outboxEvent) { }
  }

  interface Payload { }
}
