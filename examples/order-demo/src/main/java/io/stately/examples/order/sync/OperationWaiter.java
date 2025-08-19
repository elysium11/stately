package io.stately.examples.order.sync;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.*;

public class OperationWaiter {

  public static final class Result {
    public final boolean success;
    public final String details;
    public Result(boolean success, String details) {
      this.success = success; this.details = details;
    }
  }

  private final ConcurrentMap<String, CompletableFuture<Result>> waiters = new ConcurrentHashMap<>();

  public CompletableFuture<Result> register(String operationId) {
    var f = new CompletableFuture<Result>();
    var prev = waiters.putIfAbsent(operationId, f);
    return prev != null ? prev : f;
  }

  public void complete(String operationId, Result r) {
    var f = waiters.remove(operationId);
    if (f != null) f.complete(r);
  }

  public Result await(String operationId, Duration timeout) throws TimeoutException, InterruptedException {
    try {
      return register(operationId).get(timeout.toMillis(), TimeUnit.MILLISECONDS);
    } catch (ExecutionException e) {
      throw new RuntimeException(e.getCause());
    }
  }
}
