package be.kdg.team22.tictactoeservice.events;

import be.kdg.team22.tictactoeservice.domain.game.Game;
import be.kdg.team22.tictactoeservice.domain.player.PlayerRole;

import java.util.List;

public record AiMoveRequestedEvent(
        String gameId,
        String gameName,
        List<List<String>> board,
        PlayerRole currentPlayer,
        PlayerRole aiPlayer
) {
    public static AiMoveRequestedEvent from(Game game) {
        return new AiMoveRequestedEvent(
                game.id().value().toString(),
                "TICTACTOE",
                game.board().boardState(),
                game.currentRole(),
                game.aiPlayer()
        );
    }
}
