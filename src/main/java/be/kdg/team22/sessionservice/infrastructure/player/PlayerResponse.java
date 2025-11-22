package be.kdg.team22.sessionservice.infrastructure.player;

import java.util.UUID;

public record PlayerResponse(UUID id,
                             String username,
                             String email) {
}