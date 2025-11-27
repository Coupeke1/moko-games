package be.kdg.team22.sessionservice.infrastructure.lobby.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LobbyJpaRepository extends JpaRepository<LobbyEntity, UUID> {

    @Query("""
            SELECT l
            FROM LobbyEntity l
            JOIN l.invitedPlayerIds invited
            WHERE invited = :playerId
              AND l.gameId = :gameId
            """)
    List<LobbyEntity> findInvitesFromPlayerId(@Param("playerId") UUID playerId, @Param("gameId") UUID gameId);

}