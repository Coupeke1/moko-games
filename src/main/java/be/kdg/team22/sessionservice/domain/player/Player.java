package be.kdg.team22.sessionservice.domain.player;

import org.jmolecules.ddd.annotation.Entity;

@Entity
public class Player {
    private final PlayerId id;
    private final PlayerName username;
    private final String image;
    private boolean ready;

    public Player(final PlayerId id, final PlayerName username, final String image, final boolean ready) {
        this.id = id;
        this.username = username;
        this.ready = ready;
        this.image = image;
    }

    public Player(final PlayerId id, final PlayerName username, final String image) {
        this(id, username, image, false);
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

    public String image() {
        return image;
    }
}
