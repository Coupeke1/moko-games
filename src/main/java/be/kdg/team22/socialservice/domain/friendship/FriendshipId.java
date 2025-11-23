package be.kdg.team22.socialservice.domain.friendship;

import be.kdg.team22.socialservice.domain.friendship.exceptions.FriendshipNotFoundException;

import java.util.UUID;

public record FriendshipId(UUID value) {
    public FriendshipId {
        if (value == null)
            throw new IllegalArgumentException("ID cannot be null");
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

    public FriendshipNotFoundException notFound() {
        throw new FriendshipNotFoundException(value);
    }
}