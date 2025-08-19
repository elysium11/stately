package io.stately.core.store;

import io.stately.core.store.OutboxAppender.Payload;
import java.util.UUID;

public record OutboxEventImpl(
    UUID id,
    String aggregateType,
    Object aggregateId,
    String eventType,
    Payload payload,
    String operationId
) implements OutboxEvent { }
