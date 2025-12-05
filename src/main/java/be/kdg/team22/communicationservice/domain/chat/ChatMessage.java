package be.kdg.team22.communicationservice.domain.chat;

import be.kdg.team22.communicationservice.domain.chat.exceptions.MessageEmptyException;
import org.jmolecules.ddd.annotation.Entity;

import java.time.Instant;
import java.util.Objects;

@Entity
public class ChatMessage {

    private final ChatMessageId id;
    private final ChatChannelId channelId;

    private final String senderId;
    private final String content;
    private final Instant timestamp;

    public ChatMessage(ChatMessageId id,
                       ChatChannelId channelId,
                       String senderId,
                       String content,
                       Instant timestamp) {
        this.id = Objects.requireNonNull(id);
        this.channelId = Objects.requireNonNull(channelId);
        this.senderId = Objects.requireNonNull(senderId);
        this.content = Objects.requireNonNull(content);
        this.timestamp = Objects.requireNonNull(timestamp);

        if (content.isBlank()) {
            throw MessageEmptyException.BodyEmpty();
        }
    }

    public static ChatMessage newUserMessage(ChatChannelId channelId,
                                             String senderId,
                                             String content) {
        return new ChatMessage(
                ChatMessageId.create(),
                channelId,
                senderId,
                content,
                Instant.now()
        );
    }

    public static ChatMessage newAIMessage(ChatChannelId channelId,
                                           String aiModelId,
                                           String content) {
        return new ChatMessage(
                ChatMessageId.create(),
                channelId,
                "ai:" + aiModelId,
                content,
                Instant.now()
        );
    }

    public ChatMessageId getId() {
        return id;
    }

    public ChatChannelId getChannelId() {
        return channelId;
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