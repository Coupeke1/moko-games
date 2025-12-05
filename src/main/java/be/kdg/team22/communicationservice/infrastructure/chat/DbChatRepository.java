package be.kdg.team22.communicationservice.infrastructure.chat;

import be.kdg.team22.communicationservice.domain.chat.*;
import be.kdg.team22.communicationservice.infrastructure.chat.jpa.ChatChannelEntity;
import be.kdg.team22.communicationservice.infrastructure.chat.jpa.ChatMessageEntity;
import be.kdg.team22.communicationservice.infrastructure.chat.jpa.JpaChatChannelRepository;
import be.kdg.team22.communicationservice.infrastructure.chat.jpa.JpaChatMessageRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class DbChatRepository implements ChatChannelRepository {

    private final JpaChatChannelRepository channelJpa;
    private final JpaChatMessageRepository messageJpa;

    public DbChatRepository(JpaChatChannelRepository channelJpa, JpaChatMessageRepository messageJpa) {
        this.channelJpa = channelJpa;
        this.messageJpa = messageJpa;
    }

    @Override
    public Optional<ChatChannel> findById(ChatChannelId id) {
        return channelJpa.findById(id.value()).map(ChatChannelEntity::to);
    }

    @Override
    public Optional<ChatChannel> findByTypeAndReferenceId(ChatChannelType type, String referenceId) {
        return channelJpa.findByTypeAndReferenceId(type, referenceId).map(ChatChannelEntity::to);
    }

    @Override
    public void save(ChatChannel channel) {
        channelJpa.save(ChatChannelEntity.from(channel));
    }

    @Override
    public List<ChatMessage> findMessages(ChatChannelType type, String referenceId) {
        return channelJpa.findByTypeAndReferenceId(type, referenceId).map(entity -> messageJpa.findByChannelId(entity.getId()).stream().map(ChatMessageEntity::to).toList()).orElse(List.of());
    }

    @Override
    public List<ChatMessage> findMessagesSince(ChatChannelType type, String referenceId, Instant since) {
        return channelJpa.findByTypeAndReferenceId(type, referenceId).map(entity -> messageJpa.findByChannelIdAndTimestampAfter(entity.getId(), since).stream().map(ChatMessageEntity::to).toList()).orElse(List.of());
    }
}