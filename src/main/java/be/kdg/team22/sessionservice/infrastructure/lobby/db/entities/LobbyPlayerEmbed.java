package be.kdg.team22.sessionservice.infrastructure.lobby.db.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class LobbyPlayerEmbed {

    @Column(name = "player_id", nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false)
    private String username;

    protected LobbyPlayerEmbed() {
    }

    public LobbyPlayerEmbed(UUID id, String username) {
        this.id = id;
        this.username = username;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
