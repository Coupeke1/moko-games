package be.kdg.team22.sessionservice.api.lobby.models;

import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyStatus;
import be.kdg.team22.sessionservice.domain.lobby.settings.CheckersSettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record LobbyResponseModel(UUID id,
                                 UUID gameId,
                                 UUID ownerId,
                                 Set<PlayerSummaryModel> players,
                                 PlayerSummaryModel aiPlayer,
                                 int maxPlayers,
                                 LobbyStatus status,
                                 Instant createdAt,
                                 GameSettingsModel settings,
                                 UUID startedGameId) {

    public static LobbyResponseModel from(Lobby lobby) {
        GameSettingsModel settingsModel = switch (lobby.settings().gameSettings()) {
            case TicTacToeSettings t ->
                    new TicTacToeSettingsModel(t.boardSize());
            case CheckersSettings c ->
                    new CheckersSettingsModel(c.boardSize(), c.flyingKings());
        };

        PlayerSummaryModel botModel = null;
        if (lobby.hasBot())
            botModel = PlayerSummaryModel.from(lobby.bot());

        return new LobbyResponseModel(lobby.id().value(), lobby.gameId().value(), lobby.ownerId().value(), lobby.players().stream().map(PlayerSummaryModel::from).collect(Collectors.toSet()), botModel, lobby.settings().maxPlayers(), lobby.status(), lobby.createdAt(), settingsModel, lobby.startedGameId().map(GameId::value).orElse(null));
    }
}