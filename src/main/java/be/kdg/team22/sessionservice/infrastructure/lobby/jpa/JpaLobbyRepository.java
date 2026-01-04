package be.kdg.team22.sessionservice.infrastructure.lobby.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaLobbyRepository extends JpaRepository<LobbyEntity, UUID> {

    Optional<LobbyEntity> findByStartedGameId(final UUID startedGameId);


    @Query(value = """
                SELECT DISTINCT l.*
                FROM lobbies l
                INNER JOIN session_service.lobby_players p ON l.id = p.lobby_id
                WHERE p.player_id = :playerId
                  AND l.status IN ('OPEN', 'STARTED')
                ORDER BY l.updated_at DESC
                LIMIT 1
            """, nativeQuery = true)
    Optional<LobbyEntity> findByPlayerId(@Param("playerId") UUID playerId);

    @Query("""
            SELECT l
            FROM LobbyEntity l
            JOIN l.invitedPlayerIds invited
            WHERE invited = :playerId
              AND l.gameId = :gameId
            """)
    List<LobbyEntity> findInvitesFromPlayerId(@Param("playerId") UUID playerId, @Param("gameId") UUID gameId);

}