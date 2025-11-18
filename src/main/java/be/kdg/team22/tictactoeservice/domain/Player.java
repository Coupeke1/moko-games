package be.kdg.team22.tictactoeservice.domain;

public enum Player {
    X, O;

    public Player getOpposite() {
        return this == X ? O : X;
    }
}
