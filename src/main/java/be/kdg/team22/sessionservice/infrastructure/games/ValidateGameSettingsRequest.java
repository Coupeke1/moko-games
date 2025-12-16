package be.kdg.team22.sessionservice.infrastructure.games;

import java.util.Map;

public record ValidateGameSettingsRequest(Map<String, Object> settings) {
}