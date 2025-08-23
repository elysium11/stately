package io.stately.examples.order.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import io.stately.examples.order.fsm.persistence.MapPayload;
import io.stately.examples.order.fsm.persistence.OutboxEntity;
import io.stately.examples.order.fsm.persistence.OutboxEntity.MapPayloadReadingConverter;
import io.stately.examples.order.fsm.persistence.OutboxEntity.MapPayloadWritingConverter;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PGobject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import java.util.Map;

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
//        new ImmutableMapToJsonb(objectMapper),
//        new JsonbToImmutableMap(objectMapper),
//        new MapPayloadToJsonb(objectMapper),
//        new JsonbToMapPayload(objectMapper)
        new MapPayloadReadingConverter(objectMapper),
        new MapPayloadWritingConverter(objectMapper)
    ));
  }

  static class ImmutableMapToJsonb implements Converter<ImmutableMap<?, ?>, PGobject> {

    private final ObjectMapper om;

    ImmutableMapToJsonb(ObjectMapper om) { this.om = om; }

    @Override
    public PGobject convert(ImmutableMap<?, ?> source) {
      try {
        PGobject pg = new PGobject();
        pg.setType("jsonb");
        pg.setValue(om.writeValueAsString(source));
        return pg;
      } catch (Exception e) {
        throw new IllegalArgumentException(e);
      }
    }
  }

  @RequiredArgsConstructor
  static class JsonbToImmutableMap implements Converter<PGobject, ImmutableMap<?, ?>> {

    private final ObjectMapper om;

    @Override
    public ImmutableMap<?, ?> convert(PGobject source) {
      try {
        Map<String, Object> map = om.readValue(
            source.getValue(),
            new TypeReference<ImmutableMap<String, Object>>() { }
        );
        return ImmutableMap.copyOf(map);
      } catch (Exception e) {
        throw new IllegalArgumentException(e);
      }
    }
  }

  @RequiredArgsConstructor
  static class MapPayloadToJsonb implements Converter<MapPayload, PGobject> {

    private final ObjectMapper om;

    @Override
    public PGobject convert(MapPayload source) {
      try {
        PGobject pg = new PGobject();
        pg.setType("jsonb");
        pg.setValue(om.writeValueAsString(source.data()));
        return pg;
      } catch (Exception e) {
        throw new IllegalArgumentException(e);
      }
    }
  }

  @RequiredArgsConstructor
  static class JsonbToMapPayload implements Converter<PGobject, MapPayload> {

    private final ObjectMapper om;

    @Override
    public MapPayload convert(PGobject source) {
      try {
        Map<String, Object> map = om.readValue(source.getValue(), new TypeReference<>() { });
        return MapPayload.of(ImmutableMap.copyOf(map));
      } catch (Exception e) {
        throw new IllegalArgumentException(e);
      }
    }
  }
}
