package be.kdg.team22.communicationservice.infrastructure.chat.jpa;

import be.kdg.team22.communicationservice.domain.chat.ChatChannelId;
import be.kdg.team22.communicationservice.domain.chat.ChatMessage;
import be.kdg.team22.communicationservice.domain.chat.ChatMessageId;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chat_messages")
public class ChatMessageEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private ChannelEntity channel;

    @Column(nullable = false)
    private String senderId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Instant timestamp;

    protected ChatMessageEntity() {
    }

    public ChatMessageEntity(UUID id,
                             ChannelEntity channel,
                             String senderId,
                             String content,
                             Instant timestamp) {
        this.id = id;
        this.channel = channel;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public static ChatMessageEntity from(ChatMessage msg, ChannelEntity parent) {
        return new ChatMessageEntity(
                msg.id().value(),
                parent,
                msg.senderId(),
                msg.content(),
                msg.timestamp()
        );
    }

    public ChatMessage to() {
        return new ChatMessage(ChatMessageId.from(id),new ChatChannelId(channel.getId()), senderId, content, timestamp);
    }

    public UUID getId() {
        return id;
    }

    public ChannelEntity getChannel() {
        return channel;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}