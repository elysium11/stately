package io.stately.examples.order.fsm.persistence;

import com.google.common.collect.ImmutableMap;
import io.stately.core.store.OutboxAppender.Payload;
import io.stately.core.store.OutboxEvent;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("outbox")
public record OutboxEntity(
    @Id UUID id,
    String aggregateType,
    String aggregateId,
    String eventType,
    MapPayload payload,
    String operationId,
    String status,
    Instant createdAt
) implements OutboxEvent {

  public record MapPayload(ImmutableMap<String, Object> data) implements Payload {

    public static MapPayload of(ImmutableMap<String, Object> data) {
      return new MapPayload(data);
    }
  }
}
