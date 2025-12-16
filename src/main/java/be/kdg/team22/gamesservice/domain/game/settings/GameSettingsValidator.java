package be.kdg.team22.gamesservice.domain.game.settings;

import be.kdg.team22.gamesservice.domain.game.exceptions.InvalidGameSettingsException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class GameSettingsValidator {

    private GameSettingsValidator() {
    }

    public static Map<String, Object> resolveAndValidate(
            GameSettingsDefinition definition,
            Map<String, Object> input
    ) {
        if (definition == null) throw InvalidGameSettingsException.missingDefinition();
        if (input == null) throw InvalidGameSettingsException.missingSettings();

        Map<String, Object> resolved = new HashMap<>(input);

        for (SettingDefinition def : definition.settings()) {
            if (!resolved.containsKey(def.name()) && def.defaultValue() != null) {
                resolved.put(def.name(), def.defaultValue());
            }
        }

        validateInternal(definition, resolved);

        return Map.copyOf(resolved);
    }

    private static void validateInternal(
            GameSettingsDefinition definition,
            Map<String, Object> settings
    ) {
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
                    if (!(value instanceof String s)) {
                        throw InvalidGameSettingsException.wrongType(def.name(), "a string");
                    }

                    if (def.required() && s.isBlank()) {
                        throw InvalidGameSettingsException.wrongType(def.name(), "a non-blank string");
                    }
                }

                case BOOLEAN -> {
                    if (!(value instanceof Boolean)) {
                        throw InvalidGameSettingsException.wrongType(def.name(), "a boolean");
                    }
                }

                case INTEGER -> {
                    Integer i = coerceToInt(def.name(), value);
                    settings.put(def.name(), i);

                    if (def.min() != null && i < def.min()) {
                        throw InvalidGameSettingsException.belowMin(def.name(), def.min());
                    }
                    if (def.max() != null && i > def.max()) {
                        throw InvalidGameSettingsException.aboveMax(def.name(), def.max());
                    }
                }

                case ENUM -> {
                    if (!(value instanceof String s)) {
                        throw InvalidGameSettingsException.wrongType(def.name(), "a string enum");
                    }
                    if (def.allowedValues() == null || def.allowedValues().isEmpty()) {
                        throw InvalidGameSettingsException.invalidEnum(def.name(), def.allowedValues());
                    }
                    if (!def.allowedValues().contains(s)) {
                        throw InvalidGameSettingsException.invalidEnum(def.name(), def.allowedValues());
                    }
                }
            }
        }
    }

    private static Integer coerceToInt(String name, Object value) {
        if (value instanceof Integer i) return i;

        if (value instanceof Long l) {
            if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
                throw InvalidGameSettingsException.wrongType(name, "an integer (out of range)");
            }
            return l.intValue();
        }

        if (value instanceof Number n) {
            double d = n.doubleValue();
            long asLong = n.longValue();
            if (d != (double) asLong) {
                throw InvalidGameSettingsException.wrongType(name, "an integer (no decimals)");
            }
            if (asLong < Integer.MIN_VALUE || asLong > Integer.MAX_VALUE) {
                throw InvalidGameSettingsException.wrongType(name, "an integer (out of range)");
            }
            return (int) asLong;
        }

        throw InvalidGameSettingsException.wrongType(name, "an integer");
    }
}