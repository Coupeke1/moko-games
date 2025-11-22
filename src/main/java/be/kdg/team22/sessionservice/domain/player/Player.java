package be.kdg.team22.sessionservice.domain.player;

public class Player {
    private final PlayerId id;
    private final String username;
    private final String email;

    public Player(PlayerId id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public PlayerId id() {
        return id;
    }

    public String username() {
        return username;
    }

    public String email() {
        return email;
    }
}