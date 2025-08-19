package io.stately.examples.order.fsm.persistence;

import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("fsm_transition")
public record FsmTransitionEntity(
    @Id
    Long id,
    String aggregateType,
    String aggregateId,
    String fromState,
    String toState,
    String event,
    Instant createdAt,
    ImmutableMap<String, Object> metadata,
    String uniqKey
) { }