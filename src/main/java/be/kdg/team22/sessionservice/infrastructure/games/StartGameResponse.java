package be.kdg.team22.sessionservice.infrastructure.games;

import java.util.UUID;

public record StartGameResponse(
        UUID gameInstanceId,
        String title,
        String name,
        String imageUrl,
        String frontendUrl
) {
}