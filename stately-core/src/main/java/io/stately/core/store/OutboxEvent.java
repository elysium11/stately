package io.stately.core.store;

import io.stately.core.store.OutboxAppender.Payload;
import java.util.UUID;

public interface OutboxEvent {

  UUID id();

  String aggregateType();

  Object aggregateId();

  String eventType();

  Payload payload();

  String operationId();
}
