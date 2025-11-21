package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import be.kdg.team22.sessionservice.domain.lobby.PlayerId;

import java.util.UUID;

public class PlayerNotFriendException extends RuntimeException {
    public PlayerNotFriendException(PlayerId ownerId, UUID targetId) {
        super(String.format("User '%s' cannot invite non-friend '%s' to lobby", ownerId.value(), targetId));
    }
}
