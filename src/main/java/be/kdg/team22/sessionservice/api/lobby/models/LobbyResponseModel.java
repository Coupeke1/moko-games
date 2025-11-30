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

public record LobbyResponseModel(UUID id,
                                 UUID gameId,
                                 UUID ownerId,
                                 List<PlayerSummaryModel> players,
                                 PlayerSummaryModel bot,
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

        List<PlayerSummaryModel> playerModels = lobby.players().stream().map(PlayerSummaryModel::from).sorted(Comparator.comparing(PlayerSummaryModel::username)).toList();

        PlayerSummaryModel botModel = null;
        if (lobby.hasBot())
            botModel = PlayerSummaryModel.from(lobby.bot());


        return new LobbyResponseModel(lobby.id().value(), lobby.gameId().value(), lobby.ownerId().value(), playerModels, botModel, lobby.settings().maxPlayers(), lobby.status(), lobby.createdAt(), settingsModel, lobby.startedGameId().map(GameId::value).orElse(null));
    }
}