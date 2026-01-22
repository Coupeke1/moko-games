package be.kdg.team22.checkersservice.domain.player;

public enum PlayerRole {
    BLACK(0, 'B'), WHITE(1, 'W');

    private final int order;
    private final char symbol;

    PlayerRole(final int order, final char symbol) {
        this.order = order;
        this.symbol = symbol;
    }

    public int order() {
        return order;
    }
    public char symbol() {
        return symbol;
    }
}