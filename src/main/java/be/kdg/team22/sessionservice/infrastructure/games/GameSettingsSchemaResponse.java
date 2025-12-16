package be.kdg.team22.sessionservice.infrastructure.games;

import java.util.List;
import java.util.Map;

public record GameSettingsSchemaResponse(
        List<SettingDefinitionModel> settings,
        Map<String, Object> defaults
) {
    public record SettingDefinitionModel(
            String name,
            String type,
            boolean required,
            Integer min,
            Integer max,
            List<String> allowedValues,
            Object defaultValue
    ) {
    }
}