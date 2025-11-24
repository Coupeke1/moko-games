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
    @AttributeOverrides({@AttributeOverride(name = "id", column = @Column(name = "player_id", nullable = false)), @AttributeOverride(name = "username", column = @Column(name = "username", nullable = false))})
    private Set<PlayerEmbed> players;

    @ElementCollection
    @CollectionTable(name = "lobby_invited_players", schema = "session_service", joinColumns = @JoinColumn(name = "lobby_id"))
    @Column(name = "invited_player_id", nullable = false)
    private Set<UUID> invitedPlayerIds;

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

    protected LobbyEntity() {
    }

    public LobbyEntity(UUID id, UUID gameId, UUID ownerId, Set<PlayerEmbed> players, Set<UUID> invitedPlayerIds, LobbySettings settings, LobbyStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.gameId = gameId;
        this.ownerId = ownerId;
        this.players = players;
        this.invitedPlayerIds = invitedPlayerIds;
        this.settings = settings;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static LobbyEntity fromDomain(final Lobby lobby) {
        Set<PlayerEmbed> players = lobby.players().stream().map(player -> new PlayerEmbed(player.id().value(), player.username().value())).collect(Collectors.toSet());
        Set<UUID> invitedPlayers = lobby.invitedPlayers().stream().map(PlayerId::value).collect(Collectors.toSet());

        return new LobbyEntity(lobby.id().value(), lobby.gameId().value(), lobby.ownerId().value(), players, invitedPlayers, lobby.settings(), lobby.status(), lobby.createdAt(), lobby.updatedAt());
    }


    public Lobby toDomain() {
        List<Player> players = this.players.stream().map(player -> new Player(PlayerId.from(player.id()), PlayerName.from(player.username()))).collect(Collectors.toList());
        Set<PlayerId> invitedPlayers = invitedPlayerIds.stream().map(PlayerId::from).collect(Collectors.toSet());

        return new Lobby(LobbyId.from(id), GameId.from(gameId), PlayerId.from(ownerId), players, invitedPlayers, settings, status, createdAt, updatedAt);
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
}