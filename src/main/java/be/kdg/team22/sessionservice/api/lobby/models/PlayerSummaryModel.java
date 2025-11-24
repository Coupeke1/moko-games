package be.kdg.team22.sessionservice.api.lobby.models;

import java.util.UUID;

public record PlayerSummaryModel(UUID id,
                                 String username) {}
