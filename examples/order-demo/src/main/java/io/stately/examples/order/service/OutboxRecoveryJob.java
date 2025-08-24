package io.stately.examples.order.service;

import io.stately.core.store.OutboxEvent;
import io.stately.core.store.OutboxProcessor;
import io.stately.examples.order.fsm.persistence.OutboxEntity;
import io.stately.examples.order.fsm.persistence.OutboxRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class OutboxRecoveryJob {

  private final OutboxRepository repo;
  private final OutboxProcessor dispatcher;

  @Scheduled(fixedDelay = 200)
  public void tick() {
    List<OutboxEntity> batch = repo.pickBatch(50);
    dispatcher.processOutboxEvents(new ArrayList<OutboxEvent>(batch));
  }
}
