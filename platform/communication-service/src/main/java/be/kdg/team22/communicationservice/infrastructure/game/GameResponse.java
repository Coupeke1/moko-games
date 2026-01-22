package be.kdg.team22.communicationservice.infrastructure.game;

import java.util.UUID;

public record GameResponse(UUID id,
                           String name,
                           String title,
                           String description) {}

