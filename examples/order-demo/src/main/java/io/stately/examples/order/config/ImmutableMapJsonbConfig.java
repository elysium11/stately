package io.stately.examples.order.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import io.stately.examples.order.fsm.persistence.OutboxEntity.MapPayload;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.postgresql.util.PGobject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;

import java.util.List;
import java.util.Map;

@Configuration
public class ImmutableMapJsonbConfig {

  private final ObjectMapper objectMapper;

  public ImmutableMapJsonbConfig(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Bean
  public JdbcCustomConversions jdbcCustomConversions() {
    return new JdbcCustomConversions(List.of(
        new ImmutableMapToJsonb(objectMapper),
        new JsonbToImmutableMap(objectMapper)
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

  static class JsonbToImmutableMap implements Converter<PGobject, ImmutableMap<?, ?>> {

    private final ObjectMapper om;

    JsonbToImmutableMap(ObjectMapper om) { this.om = om; }

    @Override
    public ImmutableMap<?, ?> convert(PGobject source) {
      try {
        Map<String, Object> map = om.readValue(
            source.getValue(),
            new TypeReference<Map<String, Object>>() { }
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
    public PGobject convert(@NotNull MapPayload source) {
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
