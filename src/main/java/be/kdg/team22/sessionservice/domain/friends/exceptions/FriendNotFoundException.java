package be.kdg.team22.sessionservice.domain.friends.exceptions;

import be.kdg.team22.sessionservice.domain.friends.FriendId;

public class FriendNotFoundException extends RuntimeException {
    public FriendNotFoundException(FriendId id) {
        super(String.format("Friend with id '%s' was not found", id));
    }
}
