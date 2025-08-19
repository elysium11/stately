package io.stately.core;

@FunctionalInterface
public interface Action<A> {

  void apply(A aggregate) throws Exception;
}