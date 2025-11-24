package be.kdg.team22.sessionservice.domain.player.exceptions;

import be.kdg.team22.sessionservice.domain.player.PlayerId;

public class PlayerNotFriendException extends RuntimeException {
    public PlayerNotFriendException(PlayerId ownerId, PlayerId targetId) {
        super(String.format("User '%s' cannot invite non-friend '%s' to lobby", ownerId.value(), targetId.value()));
    }
}
