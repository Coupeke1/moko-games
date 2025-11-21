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
    private final Set<PlayerId> invitedPlayers;

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

        this.players = new ArrayList<>();
        players.forEach(pid -> this.players.add(new LobbyPlayer(pid.value(), "unknown")));

        this.invitedPlayers = new HashSet<>();

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
        this.players.add(new LobbyPlayer(owner.value(), "owner"));

        this.invitedPlayers = new HashSet<>();
    }

    public void acceptInvite(final LobbyPlayer player) {
        ensureModifiable();

        PlayerId pid = PlayerId.from(player.id());

        if (!invitedPlayers.contains(pid))
            throw new InviteNotFoundException(id.value(), pid.value());

        if (isFull())
            throw new LobbyFullException(id.value());

        if (isInLobby(pid))
            throw new PlayerAlreadyInLobbyException(pid.value(), id.value());

        invitedPlayers.remove(pid);
        players.add(player);
        updatedAt = Instant.now();
    }

    public void invitePlayer(PlayerId actingUser, PlayerId target) {
        ensureOwner(actingUser);
        ensureModifiable();

        if (isInLobby(target))
            throw new PlayerAlreadyInLobbyException(target.value(), id.value());

        invitedPlayers.add(target);
        updatedAt = Instant.now();
    }

    public void invitePlayers(PlayerId actingUser, Collection<PlayerId> targets) {
        ensureOwner(actingUser);
        ensureModifiable();

        for (PlayerId target : targets) {
            if (!isInLobby(target)) {
                invitedPlayers.add(target);
            }
        }

        updatedAt = Instant.now();
    }

    public void removePlayer(PlayerId actingUser, PlayerId target) {
        ensureOwner(actingUser);
        ensureModifiable();

        if (target.equals(owner))
            throw new CannotRemoveOwnerException(id.value());

        boolean removed = players.removeIf(p -> p.id().equals(target.value()));

        if (!removed)
            throw new PlayerNotInLobbyException(target, id.value());

        updatedAt = Instant.now();
    }

    public void removePlayers(PlayerId actingUser, Collection<PlayerId> targets) {
        ensureOwner(actingUser);
        ensureModifiable();

        if (targets.contains(owner))
            throw new CannotRemoveOwnerException(id.value());

        Set<UUID> removeIds = targets.stream()
                .map(PlayerId::value)
                .collect(HashSet::new, HashSet::add, HashSet::addAll);

        players.removeIf(p -> removeIds.contains(p.id()));

        updatedAt = Instant.now();
    }

    public void start() {
        if (status != LobbyStatus.OPEN)
            throw new LobbyAlreadyStartedException();

        status = LobbyStatus.STARTED;
        updatedAt = Instant.now();
    }

    public void close(final PlayerId actingUser) {
        ensureOwner(actingUser);
        ensureModifiable();
        status = LobbyStatus.CLOSED;
        updatedAt = Instant.now();
    }

    public void changeSettings(final PlayerId actingUser, final LobbySettings newSettings) {
        ensureOwner(actingUser);
        ensureModifiable();

        if (players.size() > newSettings.maxPlayers())
            throw new MaxPlayersTooSmallException(players.size(), newSettings.maxPlayers());

        this.settings = newSettings;
        updatedAt = Instant.now();
    }

    private boolean isInLobby(PlayerId pid) {
        return players.stream().anyMatch(p -> p.id().equals(pid.value()));
    }

    private boolean isFull() {
        return players.size() >= settings.maxPlayers();
    }

    private void ensureOwner(PlayerId actingUser) {
        if (!owner.equals(actingUser))
            throw new NotLobbyOwnerException(actingUser);
    }

    private void ensureModifiable() {
        if (status == LobbyStatus.CLOSED || status == LobbyStatus.STARTED)
            throw new LobbyStateInvalidException(id.value(), status.name());
    }

    public boolean isInvited(PlayerId pid) {
        return invitedPlayers.contains(pid);
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

    public Set<PlayerId> invitedPlayers() {
        return Set.copyOf(invitedPlayers);
    }
}
