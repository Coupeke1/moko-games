package be.kdg.team22.sessionservice.infrastructure.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.LobbyModel;

import java.util.UUID;

public record LobbyMessage(UUID userId,
                           String queue,
                           LobbyModel payload) {}
