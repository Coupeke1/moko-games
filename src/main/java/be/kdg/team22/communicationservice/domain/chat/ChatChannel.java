package be.kdg.team22.communicationservice.domain.chat;

import be.kdg.team22.communicationservice.domain.chat.exceptions.AiMessageInLobbyException;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AggregateRoot
public class ChatChannel {

    private final ChatChannelId id;
    private final ChatChannelType type;
    private final String referenceId;

    private final List<ChatMessage> messages = new ArrayList<>();

    public ChatChannel(final ChatChannelId id,
                       final ChatChannelType type,
                       final String referenceId) {
        this.id = id;
        this.type = type;
        this.referenceId = referenceId;
    }

    public ChatChannel(final ChatChannelId id,
                       final ChatChannelType type,
                       final String referenceId,
                       final List<ChatMessage> messages) {
        this.id = id;
        this.type = type;
        this.referenceId = referenceId;
        this.messages.addAll(messages);
    }

    public static ChatChannel createNew(final ChatChannelType type, final String referenceId) {
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

    public List<ChatMessage> getMessagesSince(final Instant since) {
        return messages.stream()
                .filter(m -> m.getTimestamp().isAfter(since))
                .toList();
    }

    public ChatMessage postUserMessage(final String senderId, final String content) {
        ChatMessage message = ChatMessage.newUserMessage(id, senderId, content);
        messages.add(message);
        return message;
    }

    public ChatMessage postAIMessage(final String aiModelId, final String content) {
        if (type != ChatChannelType.BOT) {
            throw new AiMessageInLobbyException("BOT messages are only allowed on BOT channels");
        }
        ChatMessage message = ChatMessage.newAIMessage(id, aiModelId, content);
        messages.add(message);
        return message;
    }
}