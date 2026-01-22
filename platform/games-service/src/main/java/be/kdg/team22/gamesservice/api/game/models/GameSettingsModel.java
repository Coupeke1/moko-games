package be.kdg.team22.gamesservice.api.game.models;

import java.util.Map;

public record GameSettingsModel(
        Map<String, Object> settings
) {
}