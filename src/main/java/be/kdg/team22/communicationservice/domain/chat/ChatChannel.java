package be.kdg.team22.communicationservice.domain.chat;

import be.kdg.team22.communicationservice.domain.chat.exceptions.AiMessageInLobbyException;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@AggregateRoot
public class ChatChannel {

    private final ChatChannelId id;
    private final ChatChannelType type;
    private final String referenceId;

    private final List<ChatMessage> messages = new ArrayList<>();

    public ChatChannel(ChatChannelId id,
                       ChatChannelType type,
                       String referenceId) {
        this.id = Objects.requireNonNull(id);
        this.type = Objects.requireNonNull(type);
        this.referenceId = Objects.requireNonNull(referenceId);
    }

    public static ChatChannel createNew(ChatChannelType type, String referenceId) {
        return new ChatChannel(ChatChannelId.create(), type, referenceId);
    }

    public ChatChannelId getId() {
        return id;
    }

    public ChatChannelType getType() {
        return type;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public List<ChatMessage> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public List<ChatMessage> getMessagesSince(Instant since) {
        Objects.requireNonNull(since);
        return messages.stream()
                .filter(m -> m.getTimestamp().isAfter(since))
                .toList();
    }

    public ChatMessage postUserMessage(String senderId, String content) {
        ChatMessage message = ChatMessage.newUserMessage(id, senderId, content);
        messages.add(message);
        return message;
    }

    public ChatMessage postAIMessage(String aiModelId, String content) {
        if (type != ChatChannelType.AI) {
            throw new AiMessageInLobbyException("AI messages are only allowed on AI channels");
        }
        ChatMessage message = ChatMessage.newAIMessage(id, aiModelId, content);
        messages.add(message);
        return message;
    }
}
