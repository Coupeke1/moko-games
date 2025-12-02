package be.kdg.team22.sessionservice.api.lobby.models;

import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyStatus;
import be.kdg.team22.sessionservice.domain.lobby.settings.CheckersSettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public record LobbyModel(UUID id, UUID gameId,
                         UUID ownerId,
                         List<PlayerModel> players,
                         PlayerModel bot,
                         int maxPlayers,
                         LobbyStatus status,
                         Instant createdAt,
                         GameSettingsModel settings,
                         UUID startedGameId) {

    public static LobbyModel from(Lobby lobby) {
        GameSettingsModel settingsModel = switch (lobby.settings().gameSettings()) {
            case TicTacToeSettings t ->
                    new TicTacToeSettingsModel(t.boardSize());
            case CheckersSettings c ->
                    new CheckersSettingsModel(c.boardSize(), c.flyingKings());
        };

        List<PlayerModel> playerModels = lobby.players().stream().map(PlayerModel::from).sorted(Comparator.comparing(PlayerModel::username)).toList();

        PlayerModel botModel = null;
        if (lobby.hasBot())
            botModel = PlayerModel.from(lobby.bot());


        return new LobbyModel(lobby.id().value(), lobby.gameId().value(), lobby.ownerId().value(), playerModels, botModel, lobby.settings().maxPlayers(), lobby.status(), lobby.createdAt(), settingsModel, lobby.startedGameId().map(GameId::value).orElse(null));
    }
}