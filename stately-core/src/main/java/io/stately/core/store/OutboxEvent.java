package io.stately.core.store;

import io.stately.core.store.OutboxAppender.Payload;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public interface OutboxEvent {

  @NotNull
  UUID id();

  @NotNull
  String aggregateType();

  @NotNull
  Object aggregateId();

  @NotNull
  String eventType();

  Payload payload();

  String operationId();
}
