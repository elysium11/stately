package io.stately.spring;

import io.stately.core.store.FsmTransactionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class StatelyAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public FsmTransactionManager transactionManager(TransactionTemplate transactionTemplate) {
    return new SpringTransactionManager(transactionTemplate);
  }

//  @Bean
//  @ConditionalOnMissingBean
//  public LockingStrategy noopLocking() {
//    return (type, id) -> { };
//  }
}
