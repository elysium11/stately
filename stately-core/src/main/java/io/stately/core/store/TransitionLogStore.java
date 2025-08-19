package io.stately.core.store;

import java.time.Instant;
import java.util.Map;

public interface TransitionLogStore<S, E> {

  void record(
      String aggregateType,
      Object aggregateId,
      S from,
      S to,
      E event,
      Instant at,
      Map<String, Object> metadata,
      String idempotencyKey
  );
}