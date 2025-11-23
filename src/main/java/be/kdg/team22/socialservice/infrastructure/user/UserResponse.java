package be.kdg.team22.socialservice.infrastructure.user;

import java.util.UUID;

public record UserResponse(UUID id,
                           String username) {
}