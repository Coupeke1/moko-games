package be.kdg.team22.socialservice.friends.domain;

import java.util.UUID;

public record FriendshipId(UUID value) {
    public FriendshipId {
        if (value == null) throw new IllegalArgumentException("FriendshipId cannot be null");
    }

    public static FriendshipId newId() {
        return new FriendshipId(UUID.randomUUID());
    }

    public static FriendshipId from(UUID uuid) {
        return new FriendshipId(uuid);
    }

    public static FriendshipId fromString(String id) {
        return new FriendshipId(UUID.fromString(id));
    }
}
