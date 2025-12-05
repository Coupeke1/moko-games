package be.kdg.team22.communicationservice.domain.chat;

import be.kdg.team22.communicationservice.domain.chat.exceptions.MessageEmptyException;
import org.jmolecules.ddd.annotation.Entity;

import java.time.Instant;

@Entity
public class ChatMessage {

    private final ChatMessageId id;
    private final ChatChannelId channelId;

    private final String senderId;
    private final String content;
    private final Instant timestamp;

    public ChatMessage(final ChatMessageId id,
                       final ChatChannelId channelId,
                       final String senderId,
                       final String content,
                       final Instant timestamp) {
        this.id = id;
        this.channelId = channelId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;

        if (content.isBlank()) {
            throw MessageEmptyException.BodyEmpty();
        }
    }

    public static ChatMessage newUserMessage(final ChatChannelId channelId,
                                             final String senderId,
                                             final String content) {
        return new ChatMessage(
                ChatMessageId.create(),
                channelId,
                senderId,
                content,
                Instant.now()
        );
    }

    public static ChatMessage newAIMessage(final ChatChannelId channelId,
                                           final String aiModelId,
                                           final String content) {
        return new ChatMessage(
                ChatMessageId.create(),
                channelId,
                "bot:" + aiModelId,
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