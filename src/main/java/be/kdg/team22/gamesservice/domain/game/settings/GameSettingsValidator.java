package be.kdg.team22.gamesservice.domain.game.settings;

import java.util.Map;

public class GameSettingsValidator {

    public static void validate(
            GameSettingsDefinition definition,
            Map<String, Object> settings
    ) {
        for (SettingDefinition def : definition.settings()) {
            Object value = settings.get(def.name());

            if (def.required() && value == null) {
                throw new IllegalArgumentException("Missing setting: " + def.name());
            }

            if (value == null) continue;

            switch (def.type()) {
                case STRING -> {
                    if (!(value instanceof String)) {
                        throw new IllegalArgumentException(def.name() + " must be string");
                    }
                }
                case BOOLEAN -> {
                    if (!(value instanceof Boolean)) {
                        throw new IllegalArgumentException(def.name() + " must be boolean");
                    }
                }
                case INTEGER -> {
                    if (!(value instanceof Integer i)) {
                        throw new IllegalArgumentException(def.name() + " must be integer");
                    }
                    if (def.min() != null && i < def.min())
                        throw new IllegalArgumentException(def.name() + " < min");
                    if (def.max() != null && i > def.max())
                        throw new IllegalArgumentException(def.name() + " > max");
                }
                case ENUM -> {
                    if (!(value instanceof String s)) {
                        throw new IllegalArgumentException(def.name() + " must be string enum");
                    }
                    if (!def.allowedValues().contains(s)) {
                        throw new IllegalArgumentException(
                                def.name() + " must be one of " + def.allowedValues()
                        );
                    }
                }
            }
        }
    }
}
