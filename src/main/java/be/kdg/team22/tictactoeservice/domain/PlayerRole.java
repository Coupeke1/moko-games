package be.kdg.team22.tictactoeservice.domain;

public enum PlayerRole {
    X, O;

    public PlayerRole getOpposite() {
        return this == X ? O : X;
    }
}
