package io.stately.examples.order.fsm.persistence;

import org.springframework.data.repository.CrudRepository;

public interface FsmTransitionRepository extends CrudRepository<FsmTransitionEntity, Long> { }