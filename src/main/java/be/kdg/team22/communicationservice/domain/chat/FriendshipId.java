package be.kdg.team22.communicationservice.domain.chat;

import java.util.UUID;

public record FriendshipId(UUID value) {
    public FriendshipId {
        if (value == null)
            throw new IllegalArgumentException("FriendshipId cannot be null");
    }

    public static FriendshipId create() {
        return new FriendshipId(UUID.randomUUID());
    }

    public static FriendshipId create(String value) {
        return new FriendshipId(UUID.fromString(value));
    }

    public static FriendshipId from(UUID value) {
        return new FriendshipId(value);
    }

    public static String toReferenceId(String userId1, String userId2) {
        if (userId1.compareTo(userId2) < 0) {
            return userId1 + "-" + userId2;
        }
        return userId2 + "-" + userId1;
    }
}