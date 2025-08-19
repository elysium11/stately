package io.stately.examples.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OrderDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(OrderDemoApplication.class, args);
  }
}