package be.kdg.team22.sessionservice.infrastructure.lobby.db.users;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        Instant createdAt
) {
}