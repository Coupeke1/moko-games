package be.kdg.team22.communicationservice.infrastructure.social;

import java.util.UUID;

public record FriendResponse(UUID id, String username, String status) {
}

