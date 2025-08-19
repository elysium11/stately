package io.stately.core;

public interface LockingStrategy<ID> {

  void lock(String aggregateType, ID aggregateId);

  void unlock(String aggregateType, ID aggregateId);
}