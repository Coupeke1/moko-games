package be.kdg.team22.socialservice.domain.friendship.exceptions;

import be.kdg.team22.socialservice.domain.friendship.FriendshipId;

public class AlreadyPendingException extends RuntimeException {
    public AlreadyPendingException(FriendshipId id) {
        super(String.format("Friendship with id '%s' is already pending", id.value()));
    }
}
