package io.stately.examples.order.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.stately.examples.order.fsm.persistence.OutboxEntity.MapPayloadReadingConverter;
import io.stately.examples.order.fsm.persistence.OutboxEntity.MapPayloadWritingConverter;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;

@Configuration
public class ImmutableMapJsonbConfig {

  private final ObjectMapper objectMapper;

  public ImmutableMapJsonbConfig(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper.copy()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Bean
  public JdbcCustomConversions jdbcCustomConversions() {
    return new JdbcCustomConversions(List.of(
        new MapPayloadReadingConverter(objectMapper),
        new MapPayloadWritingConverter(objectMapper)
    ));
  }
}
