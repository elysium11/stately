package io.stately.examples.order.sync;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class OperationWaiter {

  public record Result(boolean success, String details) { }

  private final ConcurrentMap<String, CompletableFuture<Result>> waiters = new ConcurrentHashMap<>();

  public CompletableFuture<Result> register(String operationId) {
    var f = new CompletableFuture<Result>();
    var prev = waiters.putIfAbsent(operationId, f);
    return prev != null ? prev : f;
  }

  public void complete(String operationId, Result r) {
    var f = waiters.remove(operationId);
    if (f != null) { f.complete(r); }
  }

  public Result await(String operationId, Duration timeout)
      throws TimeoutException, InterruptedException {
    try {
      return register(operationId).get(timeout.toMillis(), TimeUnit.MILLISECONDS);
    } catch (ExecutionException e) {
      throw new RuntimeException(e.getCause());
    }
  }
}
