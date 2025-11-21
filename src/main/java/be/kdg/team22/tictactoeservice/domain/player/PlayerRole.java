package be.kdg.team22.tictactoeservice.domain.player;

public enum PlayerRole {
    X(0), O(1), Y(2), Z(3);

    private final int order;

    PlayerRole(final int order) {
        this.order = order;
    }

    public int order() {
        return order;
    }
}