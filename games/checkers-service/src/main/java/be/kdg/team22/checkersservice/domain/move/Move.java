package be.kdg.team22.checkersservice.domain.move;

import be.kdg.team22.checkersservice.domain.game.exceptions.OutsidePlayingFieldException;
import be.kdg.team22.checkersservice.domain.move.exceptions.NotEnoughTilesException;
import be.kdg.team22.checkersservice.domain.player.PlayerId;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.ArrayList;
import java.util.List;

@ValueObject
public record Move(PlayerId playerId, List<Integer> cells) {
    public Move {
        if (cells == null || cells.size() < 2) {
            throw new NotEnoughTilesException();
        }

        for (int cell : cells) {
            if (cell <= 0) {
                throw new OutsidePlayingFieldException();
            }
        }
    }

    public List<int[]> segments() {
        List<int[]> segments = new ArrayList<>();
        for (int i = 0; i < cells.size() - 1; i++) {
            segments.add(new int[]{cells.get(i), cells.get(i + 1)});
        }
        return segments;
    }

    public int fromCell() {
        return cells.getFirst();
    }
}