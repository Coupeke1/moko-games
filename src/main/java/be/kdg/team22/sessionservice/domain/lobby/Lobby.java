package be.kdg.team22.sessionservice.domain.lobby;

import be.kdg.team22.sessionservice.domain.lobby.exceptions.*;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.time.Instant;
import java.util.*;

@AggregateRoot
public class Lobby {
    private final LobbyId id;
    private final GameId game;
    private final PlayerId owner;

    private final List<LobbyPlayer> players;
    private final Set<UUID> invitedPlayerIds;

    private final Instant createdAt;
    private Instant updatedAt;
    private LobbyStatus status;
    private LobbySettings settings;

    public Lobby(
            final LobbyId id,
            final GameId game,
            final PlayerId owner,
            final Set<PlayerId> players,
            final LobbySettings settings,
            final LobbyStatus status,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        this.id = id;
        this.game = game;
        this.owner = owner;
        this.settings = settings;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        // domain: actual users in the lobby
        this.players = new ArrayList<>();
        players.forEach(p -> this.players.add(new LobbyPlayer(p.value(), "unknown"))); // username wordt later door mapping ingevuld

        this.invitedPlayerIds = new HashSet<>();

        if (players.stream().noneMatch(p -> p.equals(owner)))
            throw new OwnerNotFoundException(owner.value());

        if (players.size() > settings.maxPlayers())
            throw new MaxPlayersTooSmallException(players.size(), settings.maxPlayers());
    }

    public Lobby(final GameId game, final PlayerId owner, final LobbySettings settings) {
        this.id = LobbyId.create();
        this.game = game;
        this.owner = owner;
        this.settings = settings;
        this.status = LobbyStatus.OPEN;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;

        this.players = new ArrayList<>();
        this.players.add(new LobbyPlayer(owner.value(), "owner")); // username wordt later opgehaald

        this.invitedPlayerIds = new HashSet<>();
    }

    public void acceptInvite(final LobbyPlayer player) {
        ensureModifiable();

        UUID playerId = player.id();

        if (!invitedPlayerIds.contains(playerId))
            throw new InviteNotFoundException(id.value(), playerId);

        if (isFull())
            throw new LobbyFullException(id.value());

        if (isInLobby(playerId))
            throw new PlayerAlreadyInLobbyException(playerId, id.value());

        invitedPlayerIds.remove(playerId);
        players.add(player);

        updatedAt = Instant.now();
    }

    public void invitePlayer(UUID actingUserId, UUID targetPlayerId) {
        ensureOwner(actingUserId);
        ensureModifiable();

        if (isInLobby(targetPlayerId))
            throw new PlayerAlreadyInLobbyException(targetPlayerId, id.value());

        invitedPlayerIds.add(targetPlayerId);
        updatedAt = Instant.now();
    }

    public void invitePlayers(UUID actingUserId, Collection<UUID> targetPlayerIds) {
        ensureOwner(actingUserId);
        ensureModifiable();

        for (UUID targetId : targetPlayerIds) {
            if (!isInLobby(targetId)) {
                invitedPlayerIds.add(targetId);
            }
        }

        updatedAt = Instant.now();
    }

    public void removePlayer(UUID actingUserId, UUID playerId) {
        ensureOwner(actingUserId);
        ensureModifiable();

        if (playerId.equals(owner.value()))
            throw new CannotRemoveOwnerException(id.value());

        boolean removed = players.removeIf(p -> p.id().equals(playerId));

        if (!removed)
            throw new PlayerNotInLobbyException(playerId, id.value());

        updatedAt = Instant.now();
    }

    public void removePlayers(UUID actingUserId, Collection<UUID> playerIds) {
        ensureOwner(actingUserId);
        ensureModifiable();

        if (playerIds.contains(owner.value()))
            throw new CannotRemoveOwnerException(id.value());

        Set<UUID> toRemove = new HashSet<>(playerIds);

        players.removeIf(player -> toRemove.contains(player.id()));

        updatedAt = Instant.now();
    }

    public void start() {
        if (status != LobbyStatus.OPEN)
            throw new LobbyAlreadyStartedException();
        status = LobbyStatus.STARTED;
        updatedAt = Instant.now();
    }

    public void close(final PlayerId actingUser) {
        ensureOwner(actingUser.value());
        ensureModifiable();
        status = LobbyStatus.CLOSED;
        updatedAt = Instant.now();
    }

    public void changeSettings(final PlayerId actingUser, final LobbySettings newSettings) {
        ensureOwner(actingUser.value());
        ensureModifiable();

        if (players.size() > newSettings.maxPlayers())
            throw new MaxPlayersTooSmallException(players.size(), newSettings.maxPlayers());

        this.settings = newSettings;
        updatedAt = Instant.now();
    }

    private boolean isInLobby(UUID playerId) {
        return players.stream().anyMatch(p -> p.id().equals(playerId));
    }

    private boolean isFull() {
        return players.size() >= settings.maxPlayers();
    }

    private void ensureOwner(UUID actingUserId) {
        if (!owner.value().equals(actingUserId))
            throw new NotLobbyOwnerException(actingUserId);
    }

    private void ensureModifiable() {
        if (status == LobbyStatus.CLOSED || status == LobbyStatus.STARTED)
            throw new LobbyStateInvalidException(id.value(), status.name());
    }

    public boolean isInvited(UUID playerId) {
        return invitedPlayerIds.contains(playerId);
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

    public Set<LobbyPlayer> players() {
        return Set.copyOf(players);
    }

    public Set<UUID> invitedPlayers() {
        return Set.copyOf(invitedPlayerIds);
    }
}
