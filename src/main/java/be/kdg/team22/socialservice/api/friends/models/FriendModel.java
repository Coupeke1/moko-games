package be.kdg.team22.socialservice.api.friends.models;

import java.util.UUID;

public record FriendModel(
        UUID userId,
        String username,
        String status
) {
}
