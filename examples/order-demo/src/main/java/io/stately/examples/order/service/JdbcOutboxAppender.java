package io.stately.examples.order.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.stately.core.store.OutboxAppender;
import io.stately.core.store.OutboxEvent;
import io.stately.examples.order.fsm.persistence.OutboxEntity;
import io.stately.examples.order.fsm.persistence.MapPayload;
import io.stately.examples.order.fsm.persistence.OutboxRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JdbcOutboxAppender implements OutboxAppender {

  private final OutboxRepository outboxRepository;
  private final ObjectMapper om;

  @Override
  public void append(OutboxEvent outboxEvent) {
    try {
      var outboxEntity = new OutboxEntity(
          outboxEvent.id(), // id will be generated
          outboxEvent.aggregateType(),
          String.valueOf(outboxEvent.aggregateId()),
          outboxEvent.eventType(),
          MapPayload.of(om.convertValue(outboxEvent.payload(), new TypeReference<>() { })),
          outboxEvent.operationId(),
          "NEW",
          Instant.now()
      );

      outboxRepository.save(outboxEntity);

      log.debug(
          "Outbox event appended: aggregateType={}, aggregateId={}, eventType={}, operationId={}",
          outboxEvent.aggregateType(),
          outboxEvent.aggregateId(), outboxEvent.eventType(), outboxEvent.operationId()
      );
    } catch (Exception e) {
      log.error(
          "Outbox append failed for aggregateType={}, aggregateId={}, eventType={}, operationId={}",
          outboxEvent.aggregateType(),
          outboxEvent.aggregateId(), outboxEvent.eventType(), outboxEvent.operationId(), e
      );
      throw new IllegalStateException("Outbox append failed", e);
    }
  }
}
