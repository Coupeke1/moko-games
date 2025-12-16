package be.kdg.team22.userservice.domain.library.exceptions;

import java.util.UUID;

public class ExternalAchievementNotFoundException extends RuntimeException {
    public ExternalAchievementNotFoundException(UUID gameId, String key) {
        super(String.format("External game with id '%s' and key '%s' was not found", gameId, key));
    }
}
