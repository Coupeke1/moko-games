package be.kdg.team22.sessionservice.domain.lobby;

import java.util.UUID;

public record LobbyPlayer(UUID id, String username) {
    public LobbyPlayer {
        if (id == null) throw new IllegalArgumentException("LobbyPlayer id cannot be null");
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("LobbyPlayer username cannot be null or blank");
        }
    }
}
