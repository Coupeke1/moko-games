package be.kdg.team22.communicationservice.domain.chat.exceptions;

public class NotFriendsException extends RuntimeException {
    public NotFriendsException(String userId, String friendId) {
        super("User " + userId + " is not friends with " + friendId);
    }
}

