package be.kdg.team22.gamesservice.infrastructure.game.jpa.converter;

import be.kdg.team22.gamesservice.domain.game.settings.GameSettingsDefinition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;

@Converter(autoApply = false)
public class GameSettingsDefinitionJsonConverter
        implements AttributeConverter<GameSettingsDefinition, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(GameSettingsDefinition attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Could not serialize GameSettingsDefinition", e
            );
        }
    }

    @Override
    public GameSettingsDefinition convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, GameSettingsDefinition.class);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not deserialize GameSettingsDefinition", e
            );
        }
    }
}