package be.kdg.team22.communicationservice.infrastructure.user;

import java.util.UUID;

public record ProfileResponse(
        UUID id,
        String username,
        String email,
        String profileImage,
        String description
) {
}

