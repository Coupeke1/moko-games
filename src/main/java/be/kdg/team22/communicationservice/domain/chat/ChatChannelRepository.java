package be.kdg.team22.communicationservice.domain.chat;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatChannelRepository {

    Optional<ChatChannel> findById(ChatChannelId id);

    Optional<ChatChannel> findByTypeAndReferenceId(ChatChannelType type, String referenceId);

    void save(ChatChannel chatChannel);

    List<ChatMessage> findMessages(ChatChannelType type, String referenceId);

    List<ChatMessage> findMessagesSince(ChatChannelType type, String referenceId, Instant since);
}