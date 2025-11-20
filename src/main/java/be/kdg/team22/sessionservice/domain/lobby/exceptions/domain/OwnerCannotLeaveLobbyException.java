package be.kdg.team22.sessionservice.domain.lobby.exceptions.domain;

import java.util.UUID;

public class OwnerCannotLeaveLobbyException extends RuntimeException {
    public OwnerCannotLeaveLobbyException(UUID ownerId) {
        super(String.format("Owner '%s' cannot leave their own lobby", ownerId));
    }
}
