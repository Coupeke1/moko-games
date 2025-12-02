package be.kdg.team22.checkersservice.domain.game;

import be.kdg.team22.checkersservice.domain.player.PlayerRole;

public class Piece {
    private final PlayerRole color;
    private boolean isKing;

    public Piece(PlayerRole color, boolean isKing) {
        this.color = color;
        this.isKing = isKing;
    }

    @Override
    public String toString() {
        return color.symbol() + (isKing ? "K" : " ");
    }
}
