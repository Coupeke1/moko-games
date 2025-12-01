package be.kdg.team22.sessionservice.infrastructure.lobby.jpa;

import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.lobby.LobbyStatus;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.domain.player.PlayerName;
import be.kdg.team22.sessionservice.infrastructure.lobby.jpa.converters.LobbySettingsConverter;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "lobbies")
public class LobbyEntity {
    @Id
    private UUID id;

    @Column(name = "game_id", nullable = false)
    private UUID gameId;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @ElementCollection
    @CollectionTable(name = "lobby_players", schema = "session_service", joinColumns = @JoinColumn(name = "lobby_id"))
    @AttributeOverrides({@AttributeOverride(name = "id", column = @Column(name = "player_id", nullable = false)), @AttributeOverride(name = "username", column = @Column(name = "username", nullable = false)), @AttributeOverride(name = "image", column = @Column(name = "image")), @AttributeOverride(name = "ready", column = @Column(name = "ready", nullable = false))})
    @OrderBy
    private List<PlayerEmbed> players;

    @ElementCollection
    @CollectionTable(name = "lobby_invited_players", schema = "session_service", joinColumns = @JoinColumn(name = "lobby_id"))
    @Column(name = "invited_player_id", nullable = false)
    @OrderBy
    private List<UUID> invitedPlayerIds;

    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "id", column = @Column(name = "bot_id")), @AttributeOverride(name = "username", column = @Column(name = "bot_username")), @AttributeOverride(name = "image", column = @Column(name = "bot_image")), @AttributeOverride(name = "ready", column = @Column(name = "bot_ready")), @AttributeOverride(name = "isAi", column = @Column(name = "bot_is_ai"))})
    private BotEmbed bot;

    @Convert(converter = LobbySettingsConverter.class)
    @Column(name = "settings", nullable = false, columnDefinition = "TEXT")
    private LobbySettings settings;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LobbyStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column(name = "started_game_id")
    private UUID startedGameId;

    protected LobbyEntity() {
    }

    public LobbyEntity(UUID id, UUID gameId, UUID ownerId, List<PlayerEmbed> players, List<UUID> invitedPlayerIds, LobbySettings settings, LobbyStatus status, Instant createdAt, Instant updatedAt, UUID startedGameId, BotEmbed bot) {
        this.id = id;
        this.gameId = gameId;
        this.ownerId = ownerId;
        this.players = players;
        this.invitedPlayerIds = invitedPlayerIds;
        this.settings = settings;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.startedGameId = startedGameId;
        this.bot = bot;
    }

    public static LobbyEntity from(final Lobby lobby) {
        List<PlayerEmbed> players = lobby.players().stream().map(player -> new PlayerEmbed(player.id().value(), player.username().value(), player.image(), player.ready())).toList();
        List<UUID> invited = lobby.invitedPlayers().stream().map(PlayerId::value).toList();

        if (lobby.hasBot()) {
            BotEmbed bot = new BotEmbed(lobby.bot().id().value(), lobby.bot().username().value(), lobby.bot().image(), lobby.bot().ready());

            return new LobbyEntity(lobby.id().value(), lobby.gameId().value(), lobby.ownerId().value(), players, invited, lobby.settings(), lobby.status(), lobby.createdAt(), lobby.updatedAt(), lobby.startedGameId().map(GameId::value).orElse(null), bot);
        }

        return new LobbyEntity(lobby.id().value(), lobby.gameId().value(), lobby.ownerId().value(), players, invited, lobby.settings(), lobby.status(), lobby.createdAt(), lobby.updatedAt(), lobby.startedGameId().map(GameId::value).orElse(null), null);
    }

    public Lobby to() {
        List<Player> players = this.players.stream().map(player -> new Player(PlayerId.from(player.id()), PlayerName.from(player.username()), player.image(), player.ready())).toList();
        Set<PlayerId> invited = invitedPlayerIds.stream().map(PlayerId::from).collect(Collectors.toSet());

        Lobby lobby = new Lobby(LobbyId.from(id), GameId.from(gameId), PlayerId.from(ownerId), players, invited, settings, status, createdAt, updatedAt, startedGameId == null ? null : GameId.from(startedGameId));

        if (bot == null) return lobby;

        lobby.addBot(lobby.ownerId(), Player.bot(PlayerId.from(bot.id()), PlayerName.from(bot.username()), bot.image()));
        return lobby;
    }

    public UUID id() {
        return id;
    }

    public UUID gameId() {
        return gameId;
    }

    public UUID ownerId() {
        return ownerId;
    }

    public List<PlayerEmbed> players() {
        return players;
    }

    public LobbySettings settings() {
        return settings;
    }

    public LobbyStatus status() {
        return status;
    }

    public UUID startedGameId() {
        return startedGameId;
    }

    public BotEmbed bot() {
        return bot;
    }

    public List<UUID> invitedPlayerIds() {
        return invitedPlayerIds;
    }
}