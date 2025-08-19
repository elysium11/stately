package io.stately.examples.order.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.stately.examples.order.fsm.persistence.OutboxEntity;
import io.stately.examples.order.fsm.persistence.OutboxRepository;
import io.stately.examples.order.sync.OperationWaiter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class OutboxRecoveryJob {

  private final OutboxRepository repo;
  private final NamedParameterJdbcTemplate jdbc;
  private final ObjectMapper om;
  private final OperationWaiter waiter;

  @Scheduled(fixedDelay = 200)
  public void tick() {
    List<OutboxEntity> batch = repo.pickBatch(50);
    for (var e : batch) {
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
        repo.updateStatusById(e.id(), "ERROR");
        if (e.operationId() != null) {
          waiter.complete(e.operationId(), new OperationWaiter.Result(false, ex.getMessage()));
        }
      }
    }
  }
}
