package io.stately.examples.order.it;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ExtendWith(SpringExtension.class)
public abstract class BaseIntegrationTest {
  @Container
  private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16")
      .withDatabaseName("stately")
      .withUsername("stately")
      .withPassword("stately");

  @DynamicPropertySource
  static void registerDataSourceProps(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }
}