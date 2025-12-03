package be.kdg.team22.checkersservice.api.models;

import be.kdg.team22.checkersservice.domain.board.Move;
import be.kdg.team22.checkersservice.domain.player.PlayerId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record MoveModel(@Valid UUID playerId, @Positive int fromCell, @Positive int toCell) {
    public Move to() {
        return new Move(
                new PlayerId(playerId),
                fromCell,
                toCell
        );
    }
}