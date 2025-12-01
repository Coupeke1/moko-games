package be.kdg.team22.sessionservice.domain.lobby;

import be.kdg.team22.sessionservice.domain.lobby.exceptions.*;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
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
    private Player bot;
    private Instant updatedAt;

    private LobbyStatus status;
    private LobbySettings settings;

    private GameId startedGameId;

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

        if (players.stream().noneMatch(player -> player.id().equals(owner)))
            throw new OwnerNotFoundException(owner.value());

        if (players.size() > settings.maxPlayers())
            throw new MaxPlayersTooSmallException(players.size(), settings.maxPlayers());
    }

    public Lobby(final LobbyId id, final GameId game, final PlayerId owner, final List<Player> players, final Set<PlayerId> invitedPlayers, final LobbySettings settings, final LobbyStatus status, final Instant createdAt, final Instant updatedAt, final GameId startedGameId) {
        this(id, game, owner, players, invitedPlayers, settings, status, createdAt, updatedAt);
        this.startedGameId = startedGameId;
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

    public void acceptInvite(final Player target) {
        ensureModifiable();

        if (!invitedPlayerIds.contains(target.id()))
            throw new InviteNotFoundException(id, target.id());

        if (players.size() >= settings.maxPlayers())
            throw new LobbyFullException(id);

        if (players.stream().anyMatch(player -> player.id().equals(target.id())))
            throw new PlayerAlreadyInLobbyException(target.id(), id);

        invitedPlayerIds.remove(target.id());
        players.add(target);
        updatedAt = Instant.now();
    }

    public void invitePlayer(final PlayerId ownerId, final PlayerId targetId) {
        ensureOwner(ownerId);
        ensureModifiable();

        if (players.stream().anyMatch(player -> player.id().equals(targetId)))
            throw new PlayerAlreadyInLobbyException(targetId, id);

        if (invitedPlayerIds.stream().anyMatch(player -> player.equals(targetId)))
            throw new PlayerAlreadyInvitedException(targetId, id);

        invitedPlayerIds.add(targetId);
        updatedAt = Instant.now();
    }

    public void addBot(PlayerId ownerId, Player bot) {
        ensureOwner(ownerId);
        ensureBotConstraints();

        this.bot = bot;
    }

    private void ensureBotConstraints() {
        if (!(settings.gameSettings() instanceof TicTacToeSettings))
            throw new BotsNotSupportedException();

        if (bot != null)
            throw new TooManyBotsException();

        if (players.size() > 1)
            throw new TooManyPlayersException();
    }

    public void invitePlayers(final PlayerId ownerId, final Collection<PlayerId> targetIds) {
        ensureOwner(ownerId);
        ensureModifiable();

        for (PlayerId target : targetIds) {
            if (players.stream().anyMatch(player -> player.id().equals(target)))
                continue;

            invitedPlayerIds.add(target);
        }

        updatedAt = Instant.now();
    }

    public void removePlayer(final PlayerId ownerId, final PlayerId targetId) {
        ensureOwner(ownerId);
        ensureModifiable();

        if (targetId.equals(owner))
            throw new CannotRemoveOwnerException(id);

        boolean removed = players.removeIf(player -> player.id().equals(targetId));

        if (!removed)
            throw new PlayerNotInLobbyException(targetId, id);

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

        if (settings.maxPlayers() > 8)
            throw new TooManyPlayersException();

        this.settings = settings;
        updatedAt = Instant.now();
    }

    public void setReady(final PlayerId id) {
        ensureModifiable();

        Player existing = players.stream().filter(player -> player.id().equals(id)).findFirst().orElseThrow(() -> new PlayerNotInLobbyException(id, this.id));

        players.remove(existing);
        existing.setReady();
        players.add(existing);
        updatedAt = Instant.now();
    }

    public void setUnready(final PlayerId id) {
        ensureModifiable();

        Player existing = players.stream().filter(player -> player.id().equals(id)).findFirst().orElseThrow(() -> new PlayerNotInLobbyException(id, id()));

        players.remove(existing);
        existing.setUnready();
        players.add(existing);
        updatedAt = Instant.now();
    }

    public void ensureAllPlayersReady() {
        if (!players.stream().allMatch(Player::ready))
            throw new PlayersNotReadyException(id.value());
    }

    public void markStarted(final GameId gameInstanceId) {
        ensureModifiable();
        this.status = LobbyStatus.STARTED;
        this.startedGameId = gameInstanceId;
        this.updatedAt = Instant.now();
    }

    public void ensureOwner(final PlayerId ownerId) {
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

    public boolean hasJoined(final PlayerId id) {
        return players.stream().anyMatch(player -> player.id().equals(id));
    }

    public boolean hasBot() {
        return bot != null;
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

    public Optional<GameId> startedGameId() {
        return Optional.ofNullable(startedGameId);
    }

    public Set<Player> players() {
        return Set.copyOf(players);
    }

    public Set<PlayerId> invitedPlayers() {
        return Set.copyOf(invitedPlayerIds);
    }

    public Player bot() {
        return bot;
    }
}