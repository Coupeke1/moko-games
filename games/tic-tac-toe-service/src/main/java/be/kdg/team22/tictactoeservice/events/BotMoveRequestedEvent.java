package be.kdg.team22.tictactoeservice.events;

import be.kdg.team22.tictactoeservice.domain.game.Game;
import be.kdg.team22.tictactoeservice.domain.player.Player;
import be.kdg.team22.tictactoeservice.domain.player.PlayerRole;

import java.util.List;

public record BotMoveRequestedEvent(
        String gameId,
        String gameName,
        List<List<String>> board,
        Player currentPlayer,
        PlayerRole botPlayer,
        boolean expectResponse
) {
    public static BotMoveRequestedEvent from(Game game, boolean expectResponse) {
        return new BotMoveRequestedEvent(
                game.id().value().toString(),
                "TICTACTOE",
                game.board().boardState(),
                game.currentPlayer(),
                game.botPlayer(),
                expectResponse
        );
    }
}
