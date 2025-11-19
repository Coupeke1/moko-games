package be.kdg.team22.tictactoeservice.api.models;

import be.kdg.team22.tictactoeservice.domain.Game;
import be.kdg.team22.tictactoeservice.domain.GameStatus;
import be.kdg.team22.tictactoeservice.domain.Player;

import java.util.UUID;

public record GameModel(
        UUID id,
        Player[][] board,
        GameStatus status,
        UUID playerXId,
        UUID playerOId,
        Player currentPlayer
) {
    public static GameModel fromDomain(Game game) {
        return new GameModel(
                game.getId().id(),
                game.getBoard().getGrid(),
                game.getStatus(),
                game.getPlayerXId().id(),
                game.getPlayerOId().id(),
                game.getCurrentPlayer()
        );
    }
}
