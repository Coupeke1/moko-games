package be.kdg.team22.communicationservice.infrastructure.chat;

import be.kdg.team22.communicationservice.domain.chat.*;
import be.kdg.team22.communicationservice.infrastructure.chat.jpa.ChannelEntity;
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

    public DbChatRepository(final JpaChatChannelRepository channelJpa,final JpaChatMessageRepository messageJpa) {
        this.channelJpa = channelJpa;
        this.messageJpa = messageJpa;
    }

    @Override
    public Optional<Channel> findById(final ChatChannelId id) {
        return channelJpa.findById(id.value()).map(ChannelEntity::to);
    }

    @Override
    public Optional<Channel> findByTypeAndReferenceId(final ChatChannelType type, final String referenceId) {
        return channelJpa.findByTypeAndReferenceId(type, referenceId).map(ChannelEntity::to);
    }

    @Override
    public void save(final Channel channel) {
        channelJpa.save(ChannelEntity.from(channel));
    }

    @Override
    public List<ChatMessage> findMessages(final ChatChannelType type,final String referenceId) {
        return channelJpa.findByTypeAndReferenceId(type, referenceId).map(entity -> messageJpa.findByChannelId(entity.getId()).stream().map(ChatMessageEntity::to).toList()).orElse(List.of());
    }

    @Override
    public List<ChatMessage> findMessagesSince(final ChatChannelType type,final String referenceId,final Instant since) {
        return channelJpa.findByTypeAndReferenceId(type, referenceId).map(entity -> messageJpa.findByChannelIdAndTimestampAfter(entity.getId(), since).stream().map(ChatMessageEntity::to).toList()).orElse(List.of());
    }
}