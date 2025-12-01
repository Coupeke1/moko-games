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
    @CollectionTable(name = "lobby_players", schema = "session_service",
            joinColumns = @JoinColumn(name = "lobby_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "player_id", nullable = false)),
            @AttributeOverride(name = "username", column = @Column(name = "username", nullable = false)),
            @AttributeOverride(name = "image", column = @Column(name = "image")),
            @AttributeOverride(name = "ready", column = @Column(name = "ready", nullable = false))
    })
    private Set<PlayerEmbed> players;

    @ElementCollection
    @CollectionTable(name = "lobby_invited_players", schema = "session_service",
            joinColumns = @JoinColumn(name = "lobby_id"))
    @Column(name = "invited_player_id", nullable = false)
    private Set<UUID> invitedPlayerIds;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "ai_player_id")),
            @AttributeOverride(name = "username", column = @Column(name = "ai_player_username")),
            @AttributeOverride(name = "image", column = @Column(name = "ai_player_image")),
            @AttributeOverride(name = "ready", column = @Column(name = "ai_player_ready")),
            @AttributeOverride(name = "isAi", column = @Column(name = "ai_player_is_ai"))
    })
    private PlayerAiEmbed aiPlayer;

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

    public LobbyEntity(
            UUID id,
            UUID gameId,
            UUID ownerId,
            Set<PlayerEmbed> players,
            Set<UUID> invitedPlayerIds,
            LobbySettings settings,
            LobbyStatus status,
            Instant createdAt,
            Instant updatedAt,
            UUID startedGameId,
            PlayerAiEmbed aiPlayer
    ) {
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
        this.aiPlayer = aiPlayer;
    }

    public static LobbyEntity fromDomain(final Lobby lobby) {

        Set<PlayerEmbed> players = lobby.players().stream()
                .map(player -> new PlayerEmbed(
                        player.id().value(),
                        player.username().value(),
                        player.image(),
                        player.ready()
                ))
                .collect(Collectors.toSet());

        Set<UUID> invited = lobby.invitedPlayers()
                .stream()
                .map(PlayerId::value)
                .collect(Collectors.toSet());

        PlayerAiEmbed ai = null;
        if (lobby.hasAi()) {
            ai = new PlayerAiEmbed(
                    lobby.aiPlayer().id().value(),
                    lobby.aiPlayer().username().value(),
                    lobby.aiPlayer().image(),
                    lobby.aiPlayer().ready(),
                    lobby.aiPlayer().isAi()
            );
        }

        return new LobbyEntity(
                lobby.id().value(),
                lobby.gameId().value(),
                lobby.ownerId().value(),
                players,
                invited,
                lobby.settings(),
                lobby.status(),
                lobby.createdAt(),
                lobby.updatedAt(),
                lobby.startedGameId().map(GameId::value).orElse(null),
                ai
        );
    }

    public Lobby toDomain() {

        List<Player> domainPlayers = this.players.stream()
                .map(p -> new Player(
                        PlayerId.from(p.id()),
                        PlayerName.from(p.username()),
                        p.image(),
                        p.ready()
                ))
                .collect(Collectors.toList());

        Set<PlayerId> invited = this.invitedPlayerIds.stream()
                .map(PlayerId::from)
                .collect(Collectors.toSet());

        Player bot = null;
        if (this.aiPlayer != null) {
            bot = Player.ai(
                    PlayerId.from(this.aiPlayer.id()),
                    PlayerName.from(this.aiPlayer.username()),
                    this.aiPlayer.image()
            );
        }

        return new Lobby(
                LobbyId.from(id),
                GameId.from(gameId),
                PlayerId.from(ownerId),
                domainPlayers,
                invited,
                settings,
                status,
                createdAt,
                updatedAt,
                startedGameId == null ? null : GameId.from(startedGameId),
                bot
        );
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

    public Set<PlayerEmbed> players() {
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

    public PlayerAiEmbed aiPlayer() {
        return aiPlayer;
    }

    public Set<UUID> invitedPlayerIds() {
        return invitedPlayerIds;
    }
}
