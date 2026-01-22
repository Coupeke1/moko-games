package be.kdg.team22.sessionservice.api.lobby.models;

import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyStatus;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record LobbyModel(
        UUID id,
        UUID gameId,
        UUID ownerId,
        List<PlayerModel> players,
        PlayerModel bot,
        int maxPlayers,
        LobbyStatus status,
        Instant createdAt,
        Map<String, Object> settings,
        UUID startedGameId
) {
    public static LobbyModel from(Lobby lobby) {
        List<PlayerModel> playerModels = lobby.players().stream()
                .map(PlayerModel::from)
                .sorted(Comparator.comparing(PlayerModel::username))
                .toList();

        PlayerModel botModel = lobby.hasBot() ? PlayerModel.from(lobby.bot()) : null;

        return new LobbyModel(
                lobby.id().value(),
                lobby.gameId().value(),
                lobby.ownerId().value(),
                playerModels,
                botModel,
                lobby.settings().maxPlayers(),
                lobby.status(),
                lobby.createdAt(),
                lobby.settings().gameSettings(),
                lobby.startedGameId().map(GameId::value).orElse(null)
        );
    }
}