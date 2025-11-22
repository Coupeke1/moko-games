package be.kdg.team22.sessionservice.infrastructure.lobby.db.converters;

import be.kdg.team22.sessionservice.domain.lobby.settings.GameSettings;
import be.kdg.team22.sessionservice.infrastructure.lobby.db.exceptions.SettingsConversionException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class GameSettingsConverter implements AttributeConverter<GameSettings, String> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(GameSettings attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            return SettingsConversionException.serializationError(attribute.getClass(), e).toString();
        }
    }

    @Override
    public GameSettings convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw SettingsConversionException.deserializationError(dbData, e);
        }
    }
}