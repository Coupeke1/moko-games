package be.kdg.team22.sessionservice.domain.player;

public record Player(PlayerId id, PlayerName username) {
    public Player {
        if (id == null)
            throw new IllegalArgumentException("Player id cannot be null");

        if (username == null)
            throw new IllegalArgumentException("Player username cannot be null");

    }
}
