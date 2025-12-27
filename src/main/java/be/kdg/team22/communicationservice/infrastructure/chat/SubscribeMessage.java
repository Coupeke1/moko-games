package be.kdg.team22.communicationservice.infrastructure.chat;

import java.util.UUID;

public record SubscribeMessage(UUID userId,
                               String destination,
                               String sessionId) {

}