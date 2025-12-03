package be.kdg.team22.checkersservice.domain.board;

import be.kdg.team22.checkersservice.domain.board.exceptions.InvalidMoveException;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record Move(int fromCell, int toCell) {
    public Move {
        if (fromCell <= 0 || toCell <= 0) {
            throw new InvalidMoveException("Cell numbers must be positive");
        }
    }
}