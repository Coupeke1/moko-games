package be.kdg.team22.sessionservice.infrastructure.player;

import java.util.UUID;

public record BotProfileResponse(
        UUID id,
        String username,
        String image
) {
}
