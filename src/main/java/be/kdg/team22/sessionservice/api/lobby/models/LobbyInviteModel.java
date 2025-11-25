package be.kdg.team22.sessionservice.api.lobby.models;

import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.player.Player;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public static LobbyInviteModel from(Lobby lobby, Player owner, Set<Player> invitedPlayers) {

        Set<PlayerSummaryModel> playerModels = lobby.players().stream()
                .map(PlayerSummaryModel::from)
                .collect(Collectors.toSet());

        Set<PlayerSummaryModel> invitedModels = invitedPlayers.stream()
                .map(PlayerSummaryModel::from)
                .collect(Collectors.toSet());

        return new LobbyInviteModel(
                lobby.id().value(),
                lobby.gameId().value(),
                "TODO_GAME_NAME",
                owner.id().value(),
                owner.username().value(),
                playerModels,
                invitedModels,
                lobby.settings().maxPlayers(),
                lobby.status().name(),
                lobby.createdAt()
        );

    }
}