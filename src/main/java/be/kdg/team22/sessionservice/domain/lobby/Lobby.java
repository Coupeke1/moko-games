package be.kdg.team22.sessionservice.domain.lobby;

import be.kdg.team22.sessionservice.domain.lobby.exceptions.*;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@AggregateRoot
public class Lobby {

    private final LobbyId id;
    private final GameId game;
    private final PlayerId owner;

    private final List<LobbyPlayer> players;
    private final Set<PlayerId> invitedPlayerIds;

    private final Instant createdAt;
    private Instant updatedAt;

    private LobbyStatus status;
    private LobbySettings settings;

    public Lobby(
            LobbyId id,
            GameId game,
            PlayerId owner,
            List<LobbyPlayer> players,
            Set<PlayerId> invitedPlayers,
            LobbySettings settings,
            LobbyStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.game = game;
        this.owner = owner;
        this.settings = settings;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        this.players = new ArrayList<>(players);
        this.invitedPlayerIds = new HashSet<>(invitedPlayers);

        if (players.stream().noneMatch(p -> p.id().equals(owner.value())))
            throw new OwnerNotFoundException(owner.value());

        if (players.size() > settings.maxPlayers())
            throw new MaxPlayersTooSmallException(players.size(), settings.maxPlayers());
    }

    public Lobby(
            GameId game,
            LobbyPlayer ownerPlayer,
            LobbySettings settings
    ) {
        this.id = LobbyId.create();
        this.game = game;
        this.owner = PlayerId.from(ownerPlayer.id());
        this.settings = settings;
        this.status = LobbyStatus.OPEN;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;

        this.players = new ArrayList<>();
        this.players.add(ownerPlayer);

        this.invitedPlayerIds = new HashSet<>();
    }

    public void acceptInvite(LobbyPlayer player) {
        PlayerId pid = PlayerId.from(player.id());
        ensureModifiable();

        if (!invitedPlayerIds.contains(pid))
            throw new InviteNotFoundException(id.value(), player.id());

        if (players.size() >= settings.maxPlayers())
            throw new LobbyFullException(id.value());

        if (players.stream().anyMatch(p -> p.id().equals(pid.value())))
            throw new PlayerAlreadyInLobbyException(pid.value(), id.value());

        invitedPlayerIds.remove(pid);
        players.add(player);
        updatedAt = Instant.now();
    }

    public void invitePlayer(PlayerId actingUser, PlayerId target) {
        ensureOwner(actingUser);
        ensureModifiable();

        if (players.stream().anyMatch(p -> p.id().equals(target.value())))
            throw new PlayerAlreadyInLobbyException(target.value(), id.value());

        invitedPlayerIds.add(target);
        updatedAt = Instant.now();
    }

    public void invitePlayers(PlayerId actingUser, Collection<PlayerId> targets) {
        ensureOwner(actingUser);
        ensureModifiable();

        for (PlayerId t : targets) {
            if (players.stream().noneMatch(p -> p.id().equals(t.value()))) {
                invitedPlayerIds.add(t);
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

        Set<UUID> ids = targets.stream().map(PlayerId::value).collect(Collectors.toSet());
        players.removeIf(p -> ids.contains(p.id()));

        updatedAt = Instant.now();
    }

    public void close(PlayerId actingUser) {
        ensureOwner(actingUser);
        ensureModifiable();
        status = LobbyStatus.CLOSED;
        updatedAt = Instant.now();
    }

    public void changeSettings(PlayerId actingUser, LobbySettings newSettings) {
        ensureOwner(actingUser);
        ensureModifiable();

        if (players.size() > newSettings.maxPlayers())
            throw new MaxPlayersTooSmallException(players.size(), newSettings.maxPlayers());

        settings = newSettings;
        updatedAt = Instant.now();
    }

    private void ensureOwner(PlayerId actingUser) {
        if (!owner.equals(actingUser))
            throw new NotLobbyOwnerException(actingUser);
    }

    private void ensureModifiable() {
        if (status == LobbyStatus.CLOSED || status == LobbyStatus.STARTED)
            throw new LobbyStateInvalidException(id.value(), status.name());
    }

    public boolean isInvited(PlayerId id) {
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

    public Set<LobbyPlayer> players() {
        return Set.copyOf(players);
    }

    public Set<PlayerId> invitedPlayers() {
        return Set.copyOf(invitedPlayerIds);
    }
}
