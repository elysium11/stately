package io.stately.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class StateGraph<STATE, EVENT> {

  private final STATE initial;
  private final Map<STATE, Set<STATE>> adjacencyList;
  private final Map<EVENT, Map<STATE, STATE>> byEvent;

  StateGraph(
    STATE initial,
    Map<STATE, Set<STATE>> adjacencyList,
    Map<EVENT, Map<STATE, STATE>> byEvent
  ) {
    this.initial = Objects.requireNonNull(initial);
    this.adjacencyList = Map.copyOf(adjacencyList);
    this.byEvent = Map.copyOf(byEvent);
  }

  public STATE initial() {
    return initial;
  }

  public boolean canTransition(STATE from, STATE to) {
    return adjacencyList.getOrDefault(from, Set.of()).contains(to);
  }

  public Optional<STATE> nextByEvent(STATE from, EVENT event) {
    return Optional.ofNullable(byEvent.getOrDefault(event, Map.of()).get(from));
  }

  public static final class StateGraphBuilder<S, E> {

    private S initial;
    private final Map<S, Set<S>> adjacencyList = new HashMap<>();
    private final Map<E, Map<S, S>> byEvent = new HashMap<>();

    public static <S, E> StateGraphBuilder<S, E> builder(S initial) {
      var builder = new StateGraphBuilder<S, E>();
      builder.initial = initial;
      return builder;
    }

    public StateGraphBuilder<S, E> permit(S from, S to) {
      adjacencyList.computeIfAbsent(from, k -> new HashSet<>()).add(to);
      return this;
    }

    public StateGraphBuilder<S, E> on(E event, S from, S to) {
      byEvent.computeIfAbsent(event, k -> new HashMap<>()).put(from, to);
      permit(from, to);
      return this;
    }

    public StateGraph<S, E> build() {
      return new StateGraph<>(initial, adjacencyList, byEvent);
    }
  }
}
