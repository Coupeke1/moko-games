package be.kdg.team22.sessionservice.infrastructure.lobby.db.entities;

import be.kdg.team22.sessionservice.domain.lobby.*;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.infrastructure.lobby.db.converters.LobbySettingsConverter;
import jakarta.persistence.*;

import java.time.Instant;
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
    @CollectionTable(name = "lobby_players", joinColumns = @JoinColumn(name = "lobby_id"))
    private Set<LobbyPlayerEmbed> players;

    @ElementCollection
    @CollectionTable(name = "lobby_invited_players", joinColumns = @JoinColumn(name = "lobby_id"))
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

    public LobbyEntity(
            final UUID id,
            final UUID gameId,
            final UUID ownerId,
            final Set<LobbyPlayerEmbed> players,
            final Set<UUID> invitedPlayerIds,
            final LobbySettings settings,
            final LobbyStatus status,
            final Instant createdAt,
            final Instant updatedAt
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
    }

    public static LobbyEntity fromDomain(final Lobby lobby) {

        Set<LobbyPlayerEmbed> mappedPlayers =
                lobby.players().stream()
                        .map(p -> new LobbyPlayerEmbed(p.id(), p.username()))
                        .collect(Collectors.toSet());

        return new LobbyEntity(
                lobby.id().value(),
                lobby.gameId().value(),
                lobby.ownerId().value(),
                mappedPlayers,
                lobby.invitedPlayers(),
                lobby.settings(),
                lobby.status(),
                lobby.createdAt(),
                lobby.updatedAt()
        );
    }

    public Lobby toDomain() {
        Set<PlayerId> domainPlayers =
                players.stream()
                        .map(p -> PlayerId.from(p.getId()))
                        .collect(Collectors.toSet());

        return new Lobby(
                LobbyId.from(id),
                GameId.from(gameId),
                PlayerId.from(ownerId),
                domainPlayers,
                settings,
                status,
                createdAt,
                updatedAt
        );
    }
}
