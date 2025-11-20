package be.kdg.team22.sessionservice.domain.lobby;

import org.jmolecules.ddd.annotation.AggregateRoot;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@AggregateRoot
public class Lobby {

    private final LobbyId id;
    private final GameId gameId;
    private final PlayerId ownerId;
    private final Instant createdAt;
    private final Set<PlayerId> players;
    private Instant updatedAt;
    private final LobbyStatus status;

    public Lobby(LobbyId id,
                 GameId gameId,
                 PlayerId ownerId,
                 Set<PlayerId> players,
                 LobbyStatus status,
                 Instant createdAt,
                 Instant updatedAt) {

        this.id = Objects.requireNonNull(id);
        this.gameId = Objects.requireNonNull(gameId);
        this.ownerId = Objects.requireNonNull(ownerId);
        this.players = Objects.requireNonNull(players);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);

        if (!players.contains(ownerId)) {
            throw new IllegalArgumentException("Owner must be a player in the lobby");
        }
    }

    public Lobby(GameId gameId, PlayerId ownerId) {
        this.id = LobbyId.newId();
        this.gameId = Objects.requireNonNull(gameId);
        this.ownerId = Objects.requireNonNull(ownerId);
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.status = LobbyStatus.OPEN;

        this.players = new LinkedHashSet<>();
        this.players.add(ownerId);
    }

    public void addPlayer(PlayerId playerId) {
        if (status != LobbyStatus.OPEN)
            throw new IllegalStateException("Cannot join a non-open lobby");

        if (players.contains(playerId))
            throw new IllegalStateException("Player already in lobby");

        players.add(playerId);
        updatedAt = Instant.now();
    }

    public void removePlayer(PlayerId playerId) {
        if (playerId.equals(ownerId))
            throw new IllegalStateException("Owner cannot leave their own lobby");

        players.remove(playerId);
        updatedAt = Instant.now();
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
        return Set.copyOf(players);
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
