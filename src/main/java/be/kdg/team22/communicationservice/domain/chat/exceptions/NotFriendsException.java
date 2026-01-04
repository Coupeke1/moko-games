package be.kdg.team22.communicationservice.domain.chat.exceptions;

import java.util.UUID;

public class NotFriendsException extends RuntimeException {
    public NotFriendsException(UUID userId, UUID friendId) {
        super("User " + userId + " is not friends with " + friendId);
    }
}

