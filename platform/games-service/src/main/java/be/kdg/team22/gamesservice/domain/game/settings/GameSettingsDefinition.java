package be.kdg.team22.gamesservice.domain.game.settings;

import java.util.List;

public record GameSettingsDefinition(
        List<SettingDefinition> settings
) {
}