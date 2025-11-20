package be.kdg.team22.sessionservice.domain.lobby;

import be.kdg.team22.sessionservice.domain.lobby.exceptions.*;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@AggregateRoot
public class Lobby {
    private final LobbyId id;
    private final GameId game;
    private final PlayerId owner;
    private final Set<PlayerId> players;
    private final Instant createdAt;
    private Instant updatedAt;
    private LobbyStatus status;

    public Lobby(final LobbyId id, final GameId game, final PlayerId owner, final Set<PlayerId> players, final LobbyStatus status, final Instant createdAt, final Instant updatedAt) {
        this.id = id;
        this.game = game;
        this.owner = owner;
        this.players = players;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        if (!players.contains(owner))
            throw new OwnerNotFoundException(owner.value());
    }

    public Lobby(final GameId game, final PlayerId owner) {
        this.id = LobbyId.create();
        this.game = game;
        this.owner = owner;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.status = LobbyStatus.OPEN;

        this.players = new LinkedHashSet<>();
        this.players.add(owner);
    }

    public void addPlayer(final PlayerId playerId) {
        if (status != LobbyStatus.OPEN)
            throw new CannotJoinClosedLobbyException();

        if (players.contains(playerId))
            throw new PlayerAlreadyInLobbyException(playerId.value());

        players.add(playerId);
        updatedAt = Instant.now();
    }

    public void removePlayer(final PlayerId playerId) {
        if (playerId.equals(owner))
            throw new OwnerCannotLeaveLobbyException(owner.value());

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
        return game;
    }

    public PlayerId ownerId() {
        return owner;
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