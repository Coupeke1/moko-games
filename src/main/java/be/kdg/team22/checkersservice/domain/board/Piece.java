package be.kdg.team22.checkersservice.domain.board;

import be.kdg.team22.checkersservice.domain.player.PlayerRole;

public class Piece {
    private final PlayerRole color;
    private boolean isKing;

    public Piece(final PlayerRole color, final boolean isKing) {
        this.color = color;
        this.isKing = isKing;
    }

    public PlayerRole color() {
        return color;
    }

    public boolean isKing() {
        return isKing;
    }

    public void promoteToKing() {
        isKing = true;
    }

    @Override
    public String toString() {
        return color.symbol() + (isKing ? "K" : " ");
    }
}
