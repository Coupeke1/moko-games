package be.kdg.team22.sessionservice.infrastructure.lobby.jpa;

import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class PlayerAiEmbed {
    private UUID id;
    private String username;
    private String image;
    private boolean ready;
    private boolean isAi;

    protected PlayerAiEmbed() {
    }

    public PlayerAiEmbed(UUID id, String username, String image, boolean ready, boolean isAi) {
        this.id = id;
        this.username = username;
        this.image = image;
        this.ready = ready;
        this.isAi = isAi;
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

    public boolean isAi() {
        return isAi;
    }
}