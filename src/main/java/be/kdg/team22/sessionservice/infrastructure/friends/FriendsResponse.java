package be.kdg.team22.sessionservice.infrastructure.friends;

import java.util.UUID;

public record FriendsResponse(UUID userId, String username, String status) {
}
