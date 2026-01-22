package be.kdg.team22.communicationservice.infrastructure.chat.jpa.message;


import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface JpaMessageRepository extends JpaRepository<MessageEntity, UUID> {
    List<MessageEntity> findByChannelId(UUID channelId);

    List<MessageEntity> findByChannelIdAndTimestampAfter(UUID channelId, Instant timestamp);
}