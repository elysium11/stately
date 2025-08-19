package io.stately.examples.order.fsm.persistence;

import io.stately.examples.order.fsm.persistence.OutboxEntity;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface OutboxRepository extends CrudRepository<OutboxEntity, Long> {

  @Query("SELECT * FROM outbox WHERE status = 'NEW' ORDER BY created_at LIMIT :limit FOR UPDATE SKIP LOCKED")
  List<OutboxEntity> pickBatch(int limit);

  @Modifying
  @Query("UPDATE outbox SET status = :status WHERE id = :id")
  void updateStatusById(Long id, String status);
}
