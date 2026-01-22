package be.kdg.team22.sessionservice.infrastructure.games;

import java.util.Map;

public record ValidateGameSettingsResponse(Map<String, Object> resolvedSettings) {
}