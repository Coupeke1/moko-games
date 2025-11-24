package be.kdg.team22.sessionservice.infrastructure.lobby.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class PlayerEmbed {
    @Column(name = "player_id", nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false)
    private String username;

    protected PlayerEmbed() {
    }

    public PlayerEmbed(final UUID id, final String username) {
        this.id = id;
        this.username = username;
    }

    public UUID id() {
        return id;
    }

    public String username() {
        return username;
    }
}