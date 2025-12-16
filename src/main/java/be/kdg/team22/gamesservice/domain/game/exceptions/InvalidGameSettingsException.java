package be.kdg.team22.gamesservice.domain.game.exceptions;

public class InvalidGameSettingsException extends RuntimeException {

    private InvalidGameSettingsException(String message) {
        super(message);
    }

    public static InvalidGameSettingsException missingSettings() {
        return new InvalidGameSettingsException("Game settings are missing");
    }

    public static InvalidGameSettingsException missingDefinition() {
        return new InvalidGameSettingsException("Game settings definition is missing");
    }

    public static InvalidGameSettingsException missingRequired(String name) {
        return new InvalidGameSettingsException(
                "Missing required setting: " + name
        );
    }

    public static InvalidGameSettingsException unknownSetting(String name) {
        return new InvalidGameSettingsException(
                "Unknown setting: " + name
        );
    }

    public static InvalidGameSettingsException wrongType(
            String name,
            String expected
    ) {
        return new InvalidGameSettingsException(
                name + " must be " + expected
        );
    }

    public static InvalidGameSettingsException belowMin(
            String name,
            int min
    ) {
        return new InvalidGameSettingsException(
                name + " must be >= " + min
        );
    }

    public static InvalidGameSettingsException aboveMax(
            String name,
            int max
    ) {
        return new InvalidGameSettingsException(
                name + " must be <= " + max
        );
    }

    public static InvalidGameSettingsException invalidEnum(
            String name,
            Object allowed
    ) {
        return new InvalidGameSettingsException(
                name + " must be one of " + allowed
        );
    }
}