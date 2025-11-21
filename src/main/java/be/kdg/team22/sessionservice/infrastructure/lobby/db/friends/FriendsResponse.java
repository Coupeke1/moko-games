package be.kdg.team22.sessionservice.infrastructure.lobby.db.friends;

import java.util.UUID;

public record FriendsResponse(UUID userId, String username, String status) {
}
