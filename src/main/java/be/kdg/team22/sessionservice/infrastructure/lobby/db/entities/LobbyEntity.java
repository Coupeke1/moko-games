package be.kdg.team22.sessionservice.infrastructure.lobby.db.entities;

import be.kdg.team22.sessionservice.domain.lobby.*;
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
    @Column(name = "player_id", nullable = false)
    private Set<UUID> playerIds;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LobbyStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected LobbyEntity() {
    }

    public static LobbyEntity fromDomain(Lobby lobby) {
        LobbyEntity e = new LobbyEntity();
        e.id = lobby.id().value();
        e.gameId = lobby.gameId().value();
        e.ownerId = lobby.ownerId().value();
        e.playerIds = lobby.players().stream().map(PlayerId::value).collect(Collectors.toSet());
        e.status = lobby.status();
        e.createdAt = lobby.createdAt();
        e.updatedAt = lobby.updatedAt();
        return e;
    }

    public Lobby toDomain() {
        return new Lobby(
                LobbyId.from(id),
                GameId.from(gameId),
                PlayerId.from(ownerId),
                playerIds.stream().map(PlayerId::from).collect(Collectors.toSet()),
                status,
                createdAt,
                updatedAt
        );
    }
}
