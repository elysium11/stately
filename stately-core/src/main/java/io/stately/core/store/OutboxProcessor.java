package io.stately.core.store;

import java.util.List;

public interface OutboxProcessor {

  void processOutboxEvents(List<OutboxEvent> ids);
}
