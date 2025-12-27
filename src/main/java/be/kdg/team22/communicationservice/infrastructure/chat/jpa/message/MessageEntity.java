package be.kdg.team22.communicationservice.infrastructure.chat.jpa.message;

import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelId;
import be.kdg.team22.communicationservice.domain.chat.message.Message;
import be.kdg.team22.communicationservice.domain.chat.message.MessageId;
import be.kdg.team22.communicationservice.infrastructure.chat.jpa.channel.ChannelEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chat_messages")
public class MessageEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private ChannelEntity channel;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Instant timestamp;

    protected MessageEntity() {
    }

    public MessageEntity(final UUID id, final ChannelEntity channel, final UUID userId, final String content, final Instant timestamp) {
        this.id = id;
        this.channel = channel;
        this.userId = userId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public static MessageEntity from(final Message message, final ChannelEntity parent) {
        return new MessageEntity(message.id().value(), parent, message.userId().value(), message.content(), message.timestamp());
    }

    public Message to() {
        return new Message(MessageId.from(id), ChannelId.from(channel.id()), UserId.from(userId), content, timestamp);
    }

    public UUID getId() {
        return id;
    }

    public ChannelEntity getChannel() {
        return channel;
    }

    public UUID senderId() {
        return userId;
    }

    public String content() {
        return content;
    }

    public Instant timestamp() {
        return timestamp;
    }
}