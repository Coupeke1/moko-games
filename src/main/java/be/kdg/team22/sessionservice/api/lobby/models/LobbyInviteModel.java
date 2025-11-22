package be.kdg.team22.sessionservice.api.lobby.models;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record LobbyInviteModel(UUID lobbyId,
                               UUID gameId,
                               String gameName,
                               UUID invitedById,
                               String invitedByUsername,
                               Set<PlayerSummaryModel> players,
                               Set<PlayerSummaryModel> invited,
                               int maxPlayers,
                               String status,
                               Instant invitedAt) {

}
