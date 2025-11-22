package be.kdg.team22.socialservice.domain.friends.friendship;

import be.kdg.team22.socialservice.domain.friends.friendship.exceptions.NotFoundException;

import java.util.UUID;

public record FriendshipId(UUID value) {
    public static FriendshipId create() {
        return new FriendshipId(UUID.randomUUID());
    }

    public static FriendshipId create(String value) {
        return new FriendshipId(UUID.fromString(value));
    }

    public static FriendshipId from(UUID uuid) {
        return new FriendshipId(uuid);
    }

    public NotFoundException notFound() {
        throw new NotFoundException(value);
    }
}