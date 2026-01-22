package be.kdg.team22.socketservice.infrastructure;

import java.util.UUID;

public record PrivateMessage(UUID userId,
                             String queue,
                             Object payload,
                             String reason) {}
