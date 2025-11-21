package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import java.util.UUID;

public class PlayerNotFriendException extends RuntimeException {
    public PlayerNotFriendException(UUID ownerId, UUID targetId) {
        super(String.format("User '%s' cannot invite non-friend '%s' to lobby", ownerId, targetId));
    }
}
