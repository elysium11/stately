package io.stately.core;

import io.stately.core.store.OutboxAppender.Payload;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Чистый контекст перехода: guards + декларации эмиссий (для outbox) + произвольные метаданные +
 * действия над агрегатом. Никаких прямых сайд-эффектов.
 */
public final class TransitionContext<A> {

  private final List<Guard> guards = new ArrayList<>();
  /**
   * Декларации внешних эффектов, которые попадут в outbox.
   */
  private final List<Emission<? extends Payload>> emits = new ArrayList<>();
  /**
   * Действия, которые будут применены к агрегату в рамках перехода.
   */
  private final List<Action<A>> actions = new ArrayList<>();
  /**
   * Произвольная мета: например, idempotencyKey / operationId.
   */
  private Map<String, Object> meta = new HashMap<>();

  public TransitionContext<A> guard(Guard g) {
    guards.add(g);
    return this;
  }

  /**
   * Добавить действие, которое будет применено к агрегату в рамках перехода. Действие должно
   * возвращать модифицированный агрегат (новый экземпляр для иммутабельных сущностей).
   */
  public TransitionContext<A> action(Action<A> action) {
    actions.add(action);
    return this;
  }

  /**
   * Декларировать внешний эффект с типизированным payload (без operationId).
   */
  public <P extends Payload> TransitionContext<A> emit(String type, P payload) {
    emits.add(new Emission<>(type, payload, null));
    return this;
  }

  /**
   * Декларировать внешний эффект с типизированным payload и operationId (для синхронного
   * ожидания).
   */
  public <P extends Payload> TransitionContext<A> emit(String type, P payload, String operationId) {
    emits.add(new Emission<>(type, payload, operationId));
    return this;
  }

  /**
   * Декларировать внешний эффект с типизированным event type и payload (без operationId).
   */
  public <T extends Enum<T>, P extends Payload> TransitionContext<A> emit(T eventType, P payload) {
    emits.add(new Emission<>(eventType.toString(), payload, null));
    return this;
  }

  /**
   * Декларировать внешний эффект с типизированным event type, payload и operationId (для
   * синхронного ожидания).
   */
  public <T extends Enum<T>, P extends Payload> TransitionContext<A> emit(
      T eventType,
      P payload,
      String operationId
  ) {
    emits.add(new Emission<>(eventType.toString(), payload, operationId));
    return this;
  }

  /**
   * Задать произвольные метаданные (не путать с payload эффекта).
   */
  public TransitionContext<A> metadata(Map<String, Object> m) {
    this.meta = m;
    return this;
  }

  boolean guardsOk() {
    return guards.stream().allMatch(Guard::evaluate);
  }

  List<Action<A>> actions() {
    return actions;
  }

  List<Emission<? extends Payload>> emissions() {
    return emits;
  }

  Map<String, Object> meta() {
    return meta;
  }

  public record Emission<P>(String type, P payload, String operationId) { }
}
