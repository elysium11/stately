package io.stately.examples.order.fsm.persistence;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.ImmutableMap;
import io.stately.core.store.OutboxAppender.Payload;

public record MapPayload(ImmutableMap<String, Object> data) implements Payload {

  @JsonCreator
  public static MapPayload of(ImmutableMap<String, Object> data) {
    return new MapPayload(data);
  }
}
