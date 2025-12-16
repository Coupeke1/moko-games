package be.kdg.team22.gamesservice.api.game.models;

import be.kdg.team22.gamesservice.domain.game.settings.GameSettingsDefinition;
import be.kdg.team22.gamesservice.domain.game.settings.SettingDefinition;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record GameSettingsSchemaModel(
        List<SettingDefinitionModel> settings,
        Map<String, Object> defaults
) {
    public static GameSettingsSchemaModel from(GameSettingsDefinition settingsDefinition) {
        List<SettingDefinitionModel> models = settingsDefinition.settings().stream()
                .map(SettingDefinitionModel::from)
                .toList();

        Map<String, Object> defaults = settingsDefinition.settings().stream()
                .filter(s -> s.defaultValue() != null)
                .collect(Collectors.toMap(SettingDefinition::name, SettingDefinition::defaultValue));

        return new GameSettingsSchemaModel(models, defaults);
    }

    public record SettingDefinitionModel(
            String name,
            String type,
            boolean required,
            Integer min,
            Integer max,
            List<String> allowedValues,
            Object defaultValue
    ) {
        public static SettingDefinitionModel from(SettingDefinition definition) {
            return new SettingDefinitionModel(
                    definition.name(),
                    definition.type().name(),
                    definition.required(),
                    definition.min(),
                    definition.max(),
                    definition.allowedValues(),
                    definition.defaultValue()
            );
        }
    }
}
