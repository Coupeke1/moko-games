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

    @Column(name = "image")
    private String image;

    @Column(name = "ready", nullable = false)
    private boolean ready;

    protected PlayerEmbed() {
    }

    public PlayerEmbed(UUID id, String username, String image, boolean ready) {
        this.id = id;
        this.username = username;
        this.ready = ready;
        this.image = image;
    }

    public UUID id() {
        return id;
    }

    public String username() {
        return username;
    }

    public boolean ready() {
        return ready;
    }

    public String image() {
        return image;
    }
}