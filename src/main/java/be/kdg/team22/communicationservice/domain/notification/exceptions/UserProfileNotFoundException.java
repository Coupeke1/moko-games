package be.kdg.team22.communicationservice.domain.notification.exceptions;

import java.util.UUID;

public class UserProfileNotFoundException extends RuntimeException {
    public UserProfileNotFoundException(UUID userId) {
        super("User profile not found for user ID: " + userId);
    }
}