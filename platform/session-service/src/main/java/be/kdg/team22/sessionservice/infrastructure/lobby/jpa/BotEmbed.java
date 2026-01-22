package be.kdg.team22.sessionservice.infrastructure.lobby.jpa;

import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class BotEmbed {
    private UUID id;
    private String username;
    private String image;
    private boolean ready;

    protected BotEmbed() {
    }

    public BotEmbed(UUID id, String username, String image, boolean ready) {
        this.id = id;
        this.username = username;
        this.image = image;
        this.ready = ready;
    }

    public UUID id() {
        return id;
    }

    public String username() {
        return username;
    }

    public String image() {
        return image;
    }

    public boolean ready() {
        return ready;
    }
}