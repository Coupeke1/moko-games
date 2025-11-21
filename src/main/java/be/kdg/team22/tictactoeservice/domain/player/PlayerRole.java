package be.kdg.team22.tictactoeservice.domain.player;

public enum PlayerRole {
    X(0), O(1);

    private final int order;

    PlayerRole(int order) {
        this.order = order;
    }

    public int order() {
        return order;
    }
}