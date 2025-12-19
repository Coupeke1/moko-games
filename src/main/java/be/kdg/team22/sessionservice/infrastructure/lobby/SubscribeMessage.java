package be.kdg.team22.sessionservice.infrastructure.lobby;

import java.util.UUID;

public record SubscribeMessage(UUID userId,
                               String destination,
                               String sessionId) {

}