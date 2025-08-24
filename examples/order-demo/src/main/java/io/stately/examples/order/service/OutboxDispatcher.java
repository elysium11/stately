package io.stately.examples.order.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.stately.core.store.OutboxEvent;
import io.stately.core.store.OutboxProcessor;
import io.stately.examples.order.fsm.persistence.OutboxRepository;
import io.stately.examples.order.sync.OperationWaiter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class OutboxDispatcher implements OutboxProcessor {

  private final OutboxRepository repo;
  private final ObjectMapper om;
  private final OperationWaiter waiter;

  @Override
  public void processOutboxEvents(List<OutboxEvent> events) {
    for (var e : events) {
      try {
        JsonNode payload = om.valueToTree(e.payload());
        // Integration with provider...
        boolean ok = true;
        String details = "external-ok";

        repo.updateStatusById(e.id(), "DONE");
        if (e.operationId() != null) {
          waiter.complete(e.operationId(), new OperationWaiter.Result(ok, details));
        }
      } catch (Exception ex) {
        // Leave status as NEW for scheduler retry
        log.warn("Outbox dispatch failed for event {}", e.id(), ex);
      }
    }
  }
}
