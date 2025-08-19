package io.stately.core.store;

@FunctionalInterface
public interface FsmTransactionManager {

    /**
     * Выполняет операцию в рамках транзакции
     */
    <T> T executeInTransaction(TransactionCallback<T> callback);

    @FunctionalInterface
    interface TransactionCallback<T> {
        T doInTransaction() throws Exception;
    }

    /**
     * No-op реализация для случаев, когда транзакции не нужны
     */
    class Noop implements FsmTransactionManager {
        @Override
        public <T> T executeInTransaction(TransactionCallback<T> callback) {
            try {
                return callback.doInTransaction();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
