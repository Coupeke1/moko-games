package be.kdg.team22.socketservice.infrastructure;

import java.util.UUID;

public record SubscribeMessage(UUID userId,
                               String destination,
                               String sessionId) {

}