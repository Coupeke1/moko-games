package be.kdg.team22.sessionservice.infrastructure.lobby.db.converters;

import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class LobbySettingsConverter implements AttributeConverter<LobbySettings, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(LobbySettings attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalStateException("Could not serialize LobbySettings", e);
        }
    }

    @Override
    public LobbySettings convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new IllegalStateException("Could not deserialize LobbySettings", e);
        }
    }
}
