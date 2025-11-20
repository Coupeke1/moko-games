package be.kdg.team22.sessionservice.domain.lobby;

import be.kdg.team22.sessionservice.domain.lobby.exceptions.domain.*;
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
    private final Set<PlayerId> players;
    private final Instant createdAt;
    private Instant updatedAt;
    private LobbyStatus status;

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
            throw new IllegalArgumentException("Owner must be part of players set");
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
            throw new CannotJoinClosedLobbyException();

        if (players.contains(playerId))
            throw new PlayerAlreadyInLobbyException(playerId.value());

        players.add(playerId);
        updatedAt = Instant.now();
    }

    public void removePlayer(PlayerId playerId) {
        if (playerId.equals(ownerId))
            throw new OwnerCannotLeaveLobbyException(ownerId.value());

        if (!players.contains(playerId))
            throw new PlayerNotInLobbyException(playerId.value());

        players.remove(playerId);
        updatedAt = Instant.now();
    }

    public void start() {
        if (status != LobbyStatus.OPEN)
            throw new LobbyAlreadyStartedException();

        status = LobbyStatus.STARTED;
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

    public LobbyStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public Set<PlayerId> players() {
        return Set.copyOf(players);
    }
}
