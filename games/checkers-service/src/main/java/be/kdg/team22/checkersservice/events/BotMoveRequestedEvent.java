package be.kdg.team22.checkersservice.events;

import be.kdg.team22.checkersservice.domain.game.Game;
import be.kdg.team22.checkersservice.domain.move.KingMovementMode;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;

import java.util.List;

public record BotMoveRequestedEvent(
        String gameId,
        String gameName,
        List<List<String>> board,
        PlayerRole currentPlayer,
        PlayerRole botPlayer,
        KingMovementMode kingMovementMode,
        boolean expectResponse
) {
    public static BotMoveRequestedEvent from(Game game, boolean expectResponse) {
        return new BotMoveRequestedEvent(
                game.id().value().toString(),
                "CHECKERS",
                game.board().state(),
                game.currentRole(),
                game.botPlayer(),
                game.kingMovementMode(),
                expectResponse
        );
    }
}
