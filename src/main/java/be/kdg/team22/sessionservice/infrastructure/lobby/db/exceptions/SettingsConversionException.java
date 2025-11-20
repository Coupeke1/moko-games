package be.kdg.team22.sessionservice.infrastructure.lobby.db.exceptions;

public class SettingsConversionException extends RuntimeException {

    private SettingsConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public static SettingsConversionException serializationError(Class<?> type, Throwable cause) {
        return new SettingsConversionException(
                "Failed to serialize settings of type '%s'"
                        .formatted(type.getSimpleName()),
                cause
        );
    }

    public static SettingsConversionException deserializationError(String json, Throwable cause) {
        return new SettingsConversionException(
                "Failed to deserialize settings JSON: %s"
                        .formatted(json),
                cause
        );
    }
}
