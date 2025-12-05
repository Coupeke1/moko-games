package be.kdg.team22.communicationservice.domain.chat;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ChatChannelRepository {
    Optional<Channel> findById(ChatChannelId id);

    Optional<Channel> findByTypeAndReferenceId(ChatChannelType type, String referenceId);

    void save(Channel channel);

    List<ChatMessage> findMessages(ChatChannelType type, String referenceId);

    List<ChatMessage> findMessagesSince(ChatChannelType type, String referenceId, Instant since);
}