package be.kdg.team22.checkersservice.domain.move;

import be.kdg.team22.checkersservice.domain.game.exceptions.OutsidePlayingFieldException;
import be.kdg.team22.checkersservice.domain.player.PlayerId;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record Move(PlayerId playerId, int fromCell, int toCell) {
    public Move {
        if (fromCell <= 0 || toCell <= 0) {
            throw new OutsidePlayingFieldException();
        }
    }
}