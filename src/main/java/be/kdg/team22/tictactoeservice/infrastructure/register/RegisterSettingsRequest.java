package be.kdg.team22.tictactoeservice.infrastructure.register;

import java.util.List;

public record RegisterSettingsRequest(
        String name,
        SettingType type,
        boolean required,
        Integer min,
        Integer max,
        List<String> allowedValues,
        Object defaultValue
) {}