package be.kdg.team22.gamesservice.domain.game.settings;

import java.util.List;

public record SettingDefinition(
        String name,
        SettingType type,
        boolean required,
        Integer min,
        Integer max,
        List<String> allowedValues,
        Object defaultValue
) {
}