package io.stately.core;

@FunctionalInterface
public interface Guard {

  boolean evaluate();
}