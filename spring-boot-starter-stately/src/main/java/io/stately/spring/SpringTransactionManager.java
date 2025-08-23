package io.stately.spring;

import io.stately.core.store.FsmTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class SpringTransactionManager implements FsmTransactionManager {

  private final TransactionTemplate transactionTemplate;

  public SpringTransactionManager(TransactionTemplate transactionTemplate) {
    this.transactionTemplate = transactionTemplate;
  }

  @Override
  public <T> T executeInTransaction(TransactionCallback<T> callback) {
    return transactionTemplate.execute(status -> {
      try {
        return callback.doInTransaction();
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }
}
