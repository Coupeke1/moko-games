package be.kdg.team22.sessionservice.infrastructure.lobby.db.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class PlayerEmbed {
    @Column(name = "player_id", nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false)
    private String email;

    protected PlayerEmbed() {
    }

    public PlayerEmbed(final UUID id, final String username, final String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public UUID id() {
        return id;
    }

    public String username() {
        return username;
    }

    public String email() {
        return email;
    }
}
