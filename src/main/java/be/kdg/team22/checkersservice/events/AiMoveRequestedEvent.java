package be.kdg.team22.checkersservice.events;

import be.kdg.team22.checkersservice.domain.game.Game;
import be.kdg.team22.checkersservice.domain.move.KingMovementMode;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;

public record AiMoveRequestedEvent(
        String gameId,
        String gameName,
        String[][] board,
        PlayerRole currentPlayer,
        PlayerRole aiPlayer,
        KingMovementMode kingMovementMode,
        boolean expectResponse
) {
    public static AiMoveRequestedEvent from(Game game, boolean expectResponse) {
        return new AiMoveRequestedEvent(
                game.id().value().toString(),
                "CHECKERS",
                game.board().state(),
                game.currentRole(),
                game.aiPlayer(),
                game.kingMovementMode(),
                expectResponse
        );
    }
}
