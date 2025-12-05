package be.kdg.team22.communicationservice.infrastructure.chat.jpa;


import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface JpaChatMessageRepository extends JpaRepository<ChatMessageEntity, UUID> {
    List<ChatMessageEntity> findByChannelId(UUID channelId);

    List<ChatMessageEntity> findByChannelIdAndTimestampAfter(UUID channelId, Instant timestamp);
}