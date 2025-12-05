package be.kdg.team22.communicationservice.domain.chat;

import be.kdg.team22.communicationservice.domain.chat.ai.AIResponse;
import be.kdg.team22.communicationservice.domain.chat.ai.AiChatRepository;
import be.kdg.team22.communicationservice.domain.chat.exceptions.ChatChannelNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ChatService {

    private final ChatChannelRepository channelRepository;
    private final AiChatRepository aiChatRepository;

    public ChatService(ChatChannelRepository channelRepository,
                       AiChatRepository aiChatRepository) {
        this.channelRepository = channelRepository;
        this.aiChatRepository = aiChatRepository;
    }

    public ChatMessage sendMessage(ChatChannelType type,
                                   String referenceId,
                                   String senderId,
                                   String content) {

        ChatChannel channel = channelRepository
                .findByTypeAndReferenceId(type, referenceId)
                .orElseGet(() -> ChatChannel.createNew(type, referenceId));

        // 1. user message opslaan
        ChatMessage userMessage = channel.postUserMessage(senderId, content);
        channelRepository.save(channel);

        if (type == ChatChannelType.LOBBY) {
            return userMessage;
        }

        AIResponse aiResponse = aiChatRepository.askAI(
                "platform-chatbot-v1",
                senderId,
                referenceId,
                content
        );

        ChatMessage aiMessage = channel.postAIMessage(aiResponse.model(), aiResponse.answer());
        channelRepository.save(channel);

        return aiMessage;
    }

    public List<ChatMessage> getMessages(ChatChannelType type,
                                         String referenceId,
                                         Instant since) {

        ChatChannel channel = channelRepository
                .findByTypeAndReferenceId(type, referenceId)
                .orElseThrow(() -> new ChatChannelNotFoundException(type, referenceId));

        if (since == null) {
            return channel.getMessages();
        }
        return channel.getMessagesSince(since);
    }

    public List<ChatMessage> getMessages(ChatChannelType type,
                                         String referenceId) {
        return getMessages(type, referenceId, null);
    }
}