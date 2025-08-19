package io.stately.examples.order.fsm;

import io.stately.core.StateGraph;
import io.stately.core.StateGraph.StateGraphBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderGraphConfig {

  @Bean
  StateGraph<OrderState, OrderEvent> orderGraph() {
    return StateGraphBuilder.<OrderState, OrderEvent>builder(OrderState.NEW)
        .on(OrderEvent.SUBMIT, OrderState.NEW, OrderState.CONFIRMED)
        .on(OrderEvent.SHIP, OrderState.CONFIRMED, OrderState.SHIPPED)
        .on(OrderEvent.CANCEL, OrderState.CONFIRMED, OrderState.CANCELLED)
        .build();
  }
}