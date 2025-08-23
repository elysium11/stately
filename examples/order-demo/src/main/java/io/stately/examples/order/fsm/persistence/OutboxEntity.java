package io.stately.examples.order.fsm.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.stately.core.store.OutboxEvent;
import java.time.Instant;
import java.util.UUID;
import org.postgresql.util.PGobject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.annotation.Id;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.relational.core.mapping.Table;

@Table("outbox")
public record OutboxEntity(
    @Id UUID id,
    String aggregateType,
    String aggregateId,
    String eventType,
    MapPayload payload,
    String operationId,
    String status,
    Instant createdAt
) implements OutboxEvent {

  @WritingConverter
  public static final class MapPayloadWritingConverter implements Converter<MapPayload, PGobject> {
    private final ObjectMapper objectMapper;

    public MapPayloadWritingConverter(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
    }

    @Override
    public PGobject convert(MapPayload source) {
      try {
        PGobject jsonObject = new PGobject();
        // Use "json" if your column type is JSON; use "jsonb" if it's JSONB.
        jsonObject.setType("jsonb");
        jsonObject.setValue(objectMapper.writeValueAsString(source));
        return jsonObject;
      } catch (Exception e) {
        throw new IllegalArgumentException("Failed to convert MapPayload to PGobject", e);
      }
    }
  }

  @ReadingConverter
  public static final class MapPayloadReadingConverter implements Converter<PGobject, MapPayload> {
    private final ObjectMapper objectMapper;

    public MapPayloadReadingConverter(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
    }

    @Override
    public MapPayload convert(PGobject pgObject) {
      if (pgObject.getValue() == null) return null;
      try {
        return objectMapper.readValue(pgObject.getValue(), MapPayload.class);
      } catch (Exception e) {
        throw new IllegalArgumentException("Failed to convert PGobject to MapPayload", e);
      }
    }
  }
}
