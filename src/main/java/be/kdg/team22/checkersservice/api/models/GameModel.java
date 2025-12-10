package be.kdg.team22.checkersservice.api.models;

import be.kdg.team22.checkersservice.domain.game.Game;
import be.kdg.team22.checkersservice.domain.game.GameStatus;
import be.kdg.team22.checkersservice.domain.move.KingMovementMode;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;

import java.util.List;
import java.util.UUID;

public record GameModel(
        UUID id,
        List<PlayerModel> players,
        PlayerRole aiPlayer,
        PlayerRole currentRole,
        List<List<String>> board,
        KingMovementMode kingMovementMode,
        GameStatus status
) {
    public static GameModel from(Game game) {
        return new GameModel(
                game.id().value(),
                game.players().stream().map(PlayerModel::from).toList(),
                game.aiPlayer(),
                game.currentRole(),
                game.board().state(),
                game.kingMovementMode(),
                game.status()
        );
    }
}