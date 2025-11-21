package be.kdg.team22.tictactoeservice.api.models;

import be.kdg.team22.tictactoeservice.domain.game.GameId;
import be.kdg.team22.tictactoeservice.domain.game.Move;
import be.kdg.team22.tictactoeservice.domain.player.PlayerId;

import java.util.UUID;

public record MoveModel(UUID gameId,
                        UUID playerId,
                        int row,
                        int col
) {

    public Move to() {
        return new Move(
                new GameId(gameId),
                new PlayerId(playerId),
                row,
                col
        );
    }
}
