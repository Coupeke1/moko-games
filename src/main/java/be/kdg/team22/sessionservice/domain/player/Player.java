package be.kdg.team22.sessionservice.domain.player;

public class Player {
    private final PlayerId id;
    private final PlayerName username;
    private boolean ready;

    public Player(final PlayerId id, final PlayerName username, final boolean ready) {
        this.id = id;
        this.username = username;
        this.ready = ready;
    }

    public Player(final PlayerId id, final PlayerName username) {
        this(id, username, false);
    }

    public void setReady() {
        ready = true;
    }

    public void setUnready() {
        ready = false;
    }

    public PlayerId id() {
        return id;
    }

    public PlayerName username() {
        return username;
    }

    public boolean ready() {
        return ready;
    }
}
