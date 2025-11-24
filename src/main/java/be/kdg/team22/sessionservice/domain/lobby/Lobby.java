package be.kdg.team22.sessionservice.domain.lobby;

import be.kdg.team22.sessionservice.domain.lobby.exceptions.*;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.domain.player.exceptions.PlayerAlreadyInLobbyException;
import be.kdg.team22.sessionservice.domain.player.exceptions.PlayerNotInLobbyException;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.time.Instant;
import java.util.*;

@AggregateRoot
public class Lobby {
    private final LobbyId id;
    private final GameId game;
    private final PlayerId owner;

    private final List<Player> players;
    private final Set<PlayerId> invitedPlayerIds;

    private final Instant createdAt;
    private Instant updatedAt;

    private LobbyStatus status;
    private LobbySettings settings;

    public Lobby(final LobbyId id, final GameId game, final PlayerId owner, final List<Player> players, final Set<PlayerId> invitedPlayers, final LobbySettings settings, final LobbyStatus status, final Instant createdAt, final Instant updatedAt) {
        this.id = id;
        this.game = game;
        this.owner = owner;
        this.settings = settings;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        this.players = new ArrayList<>(players);
        this.invitedPlayerIds = new HashSet<>(invitedPlayers);

        if (players.stream().noneMatch(p -> p.id().equals(owner)))
            throw new OwnerNotFoundException(owner.value());

        if (players.size() > settings.maxPlayers())
            throw new MaxPlayersTooSmallException(players.size(), settings.maxPlayers());
    }

    public Lobby(final GameId game, final Player owner, final LobbySettings settings) {
        this.id = LobbyId.create();
        this.game = game;
        this.owner = owner.id();
        this.settings = settings;
        this.status = LobbyStatus.OPEN;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;

        this.players = new ArrayList<>();
        this.players.add(owner);

        this.invitedPlayerIds = new HashSet<>();
    }

    public void acceptInvite(final Player player) {
        ensureModifiable();

        if (!invitedPlayerIds.contains(player.id()))
            throw new InviteNotFoundException(id, player.id());

        if (players.size() >= settings.maxPlayers())
            throw new LobbyFullException(id);

        if (players.stream().anyMatch(p -> p.id().equals(player.id())))
            throw new PlayerAlreadyInLobbyException(player.id(), id);

        invitedPlayerIds.remove(player.id());
        players.add(player);
        updatedAt = Instant.now();
    }

    public void invitePlayer(final PlayerId ownerId, final PlayerId targetId) {
        ensureOwner(ownerId);
        ensureModifiable();

        if (players.stream().anyMatch(p -> p.id().equals(targetId)))
            throw new PlayerAlreadyInLobbyException(targetId, id);

        invitedPlayerIds.add(targetId);
        updatedAt = Instant.now();
    }

    public void invitePlayers(final PlayerId ownerId, final Collection<PlayerId> targetIds) {
        ensureOwner(ownerId);
        ensureModifiable();

        for (PlayerId target : targetIds) {
            if (players.stream().noneMatch(player -> player.id().equals(target))) {
                invitedPlayerIds.add(target);
            }
        }

        updatedAt = Instant.now();
    }

    public void removePlayer(final PlayerId ownerId, final PlayerId targetId) {
        ensureOwner(ownerId);
        ensureModifiable();

        if (targetId.equals(this.owner))
            throw new CannotRemoveOwnerException(id);

        boolean removed = players.removeIf(player -> player.id().equals(targetId));
        if (!removed)
            throw new PlayerNotInLobbyException(targetId, id);

        updatedAt = Instant.now();
    }

    public void removePlayers(final PlayerId ownerId, final Collection<PlayerId> targetId) {
        ensureOwner(ownerId);
        ensureModifiable();

        if (targetId.contains(this.owner))
            throw new CannotRemoveOwnerException(id);

        players.removeIf(player -> targetId.stream().toList().contains(player.id()));
        updatedAt = Instant.now();
    }

    public void close(final PlayerId ownerId) {
        ensureOwner(ownerId);
        ensureModifiable();

        status = LobbyStatus.CLOSED;
        updatedAt = Instant.now();
    }

    public void changeSettings(final PlayerId ownerId, final LobbySettings settings) {
        ensureOwner(ownerId);
        ensureModifiable();

        if (players.size() > settings.maxPlayers())
            throw new MaxPlayersTooSmallException(players.size(), settings.maxPlayers());

        this.settings = settings;
        updatedAt = Instant.now();
    }

    private void ensureOwner(final PlayerId ownerId) {
        if (!owner.equals(ownerId))
            throw new NotLobbyOwnerException(ownerId);
    }

    private void ensureModifiable() {
        if (status == LobbyStatus.CLOSED || status == LobbyStatus.STARTED)
            throw new LobbyStateInvalidException(id, status.name());
    }

    public boolean isInvited(final PlayerId id) {
        return invitedPlayerIds.contains(id);
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

    public LobbySettings settings() {
        return settings;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public Set<Player> players() {
        return Set.copyOf(players);
    }

    public Set<PlayerId> invitedPlayers() {
        return Set.copyOf(invitedPlayerIds);
    }
}