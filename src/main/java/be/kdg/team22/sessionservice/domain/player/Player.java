package be.kdg.team22.sessionservice.domain.player;

import org.jmolecules.ddd.annotation.Entity;

@Entity
public class Player {
    private final PlayerId id;
    private final PlayerName username;
    private final String image;
    private final boolean isAi;
    private boolean ready;

    public Player(
            final PlayerId id,
            final PlayerName username,
            final String image,
            final boolean ready,
            final boolean isAi
    ) {
        this.id = id;
        this.username = username;
        this.image = image;
        this.ready = ready;
        this.isAi = isAi;
    }

    public Player(final PlayerId id, final PlayerName username, final String image) {
        this(id, username, image, false, false);
    }

    public Player(final PlayerId id, final PlayerName username, final String image, final boolean ready) {
        this(id, username, image, ready, false);
    }

    public static Player ai(final PlayerId id, final PlayerName name, final String image) {
        return new Player(
                id,
                name,
                image,
                true,
                true
        );
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

    public String image() {
        return image;
    }

    public boolean ready() {
        return ready;
    }

    public boolean isAi() {
        return isAi;
    }
}