package be.kdg.team22.tictactoeservice.domain.game;

import be.kdg.team22.tictactoeservice.domain.player.PlayerId;

public record Move(
        PlayerId playerId,
        int row,
        int col) {
}
