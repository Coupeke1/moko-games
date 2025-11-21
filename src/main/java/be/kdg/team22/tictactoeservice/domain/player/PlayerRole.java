package be.kdg.team22.tictactoeservice.domain.player;

public enum PlayerRole {
    X, O;

    public PlayerRole getOpposite() {
        return this == X ? O : X;
    }
}