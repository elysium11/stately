package io.stately.core.store;

public interface AggregateStateStore<A, S, ID> {

  A loadForUpdate(ID id);

  S getState(A aggregate);

  A setState(A aggregate, S newState);

  void save(A aggregate);
}
