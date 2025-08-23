package io.stately.core;

@FunctionalInterface
public interface Action<A> {

  A apply(A aggregate) throws Exception;
}
