package be.kdg.team22.communicationservice.infrastructure.chat.jpa.channel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface JpaChannelRepository extends JpaRepository<ChannelEntity, UUID> {
    @Query("""
                SELECT c
                FROM ChannelEntity c
                WHERE TREAT(c.referenceType AS LobbyReferenceTypeEntity).lobbyId = :id
            """)
    Optional<ChannelEntity> findLobbyChannel(UUID id);

    @Query("""
                SELECT c
                FROM ChannelEntity c
                JOIN TREAT(c.referenceType AS BotReferenceTypeEntity) p
                WHERE p.userId = :userId AND p.gameId = :gameId
            """)
    Optional<ChannelEntity> findBotChannel(UUID userId, UUID gameId);

    @Query("""
                SELECT c
                FROM ChannelEntity c
                JOIN TREAT(c.referenceType AS PrivateReferenceTypeEntity) p
                WHERE
                    LEAST(p.userId, p.otherUserId) = LEAST(:userId, :otherUserId)
                AND
                    GREATEST(p.userId, p.otherUserId) = GREATEST(:userId, :otherUserId)
            """)
    Optional<ChannelEntity> findPrivateChannel(UUID userId, UUID otherUserId);
}