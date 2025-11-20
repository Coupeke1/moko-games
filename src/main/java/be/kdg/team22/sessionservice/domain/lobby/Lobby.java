package be.kdg.team22.sessionservice.domain.lobby;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

public class Lobby {
    private final LobbyId id;
    private final GameId gameId;
    private final PlayerId ownerId;
    private final Set<PlayerId> players;
    private final LobbyStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Lobby(
            LobbyId id,
            GameId gameId,
            PlayerId ownerId,
            Set<PlayerId> players,
            LobbyStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.gameId = gameId;
        this.ownerId = ownerId;
        this.players = players;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Lobby(GameId gameId, PlayerId ownerId) {
        this.id = LobbyId.newId();
        this.gameId = gameId;
        this.ownerId = ownerId;
        this.players = new LinkedHashSet<>();
        this.status = LobbyStatus.OPEN;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public LobbyId id() {
        return id;
    }

    public GameId gameId() {
        return gameId;
    }

    public PlayerId ownerId() {
        return ownerId;
    }

    public Set<PlayerId> players() {
        return players;
    }

    public LobbyStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}

