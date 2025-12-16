package be.kdg.team22.gamesservice.domain.game.settings;

import be.kdg.team22.gamesservice.domain.game.exceptions.InvalidGameSettingsException;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class GameSettingsValidator {

    private GameSettingsValidator() {
    }

    public static void validate(
            GameSettingsDefinition definition,
            Map<String, Object> settings
    ) {
        if (definition == null) {
            throw InvalidGameSettingsException.missingDefinition();
        }

        if (settings == null) {
            throw InvalidGameSettingsException.missingSettings();
        }

        Set<String> allowedNames = definition.settings().stream()
                .map(SettingDefinition::name)
                .collect(Collectors.toSet());

        for (String key : settings.keySet()) {
            if (!allowedNames.contains(key)) {
                throw InvalidGameSettingsException.unknownSetting(key);
            }
        }

        for (SettingDefinition def : definition.settings()) {
            Object value = settings.get(def.name());

            if (def.required() && value == null) {
                throw InvalidGameSettingsException.missingRequired(def.name());
            }

            if (value == null) continue;

            switch (def.type()) {
                case STRING -> {
                    if (!(value instanceof String)) {
                        throw InvalidGameSettingsException.wrongType(
                                def.name(), "a string"
                        );
                    }
                }
                case BOOLEAN -> {
                    if (!(value instanceof Boolean)) {
                        throw InvalidGameSettingsException.wrongType(
                                def.name(), "a boolean"
                        );
                    }
                }
                case INTEGER -> {
                    if (!(value instanceof Integer i)) {
                        throw InvalidGameSettingsException.wrongType(
                                def.name(), "an integer"
                        );
                    }
                    if (def.min() != null && i < def.min()) {
                        throw InvalidGameSettingsException.belowMin(
                                def.name(), def.min()
                        );
                    }
                    if (def.max() != null && i > def.max()) {
                        throw InvalidGameSettingsException.aboveMax(
                                def.name(), def.max()
                        );
                    }
                }
                case ENUM -> {
                    if (!(value instanceof String s)) {
                        throw InvalidGameSettingsException.wrongType(
                                def.name(), "a string enum"
                        );
                    }
                    if (def.allowedValues() == null ||
                            !def.allowedValues().contains(s)) {
                        throw InvalidGameSettingsException.invalidEnum(
                                def.name(), def.allowedValues()
                        );
                    }
                }
            }
        }
    }
}