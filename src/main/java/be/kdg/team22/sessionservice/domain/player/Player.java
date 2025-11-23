package be.kdg.team22.sessionservice.domain.player;

public class Player {
    private final PlayerId id;
    private final PlayerName username;

    public Player(PlayerId id, PlayerName username) {
        this.id = id;
        this.username = username;
    }

    public PlayerId id() {
        return id;
    }

    public PlayerName username() {
        return username;
    }
}