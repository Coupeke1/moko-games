package be.kdg.team22.communicationservice.domain.notification.exceptions;

import java.util.UUID;

public class UserPreferencesNotFoundException extends RuntimeException {
    public UserPreferencesNotFoundException(UUID userId) {
        super("User preferences not found for user ID: " + userId);
    }
}

