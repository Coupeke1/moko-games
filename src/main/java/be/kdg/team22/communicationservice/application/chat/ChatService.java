package be.kdg.team22.communicationservice.application.chat;

import be.kdg.team22.communicationservice.domain.chat.ChatChannel;
import be.kdg.team22.communicationservice.domain.chat.ChatChannelRepository;
import be.kdg.team22.communicationservice.domain.chat.ChatChannelType;
import be.kdg.team22.communicationservice.domain.chat.ChatMessage;
import be.kdg.team22.communicationservice.domain.chat.ai.AIResponse;
import be.kdg.team22.communicationservice.domain.chat.ai.BotChatRepository;
import be.kdg.team22.communicationservice.domain.chat.exceptions.CantAutoCreateBotChannel;
import be.kdg.team22.communicationservice.domain.chat.exceptions.ChatChannelNotFoundException;
import be.kdg.team22.communicationservice.domain.chat.exceptions.NotInLobbyException;
import be.kdg.team22.communicationservice.infrastructure.lobby.ExternalSessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class ChatService {

    private final ChatChannelRepository channelRepository;
    private final BotChatRepository botChatRepository;
    private final ExternalSessionRepository sessionRepository;

    public ChatService(ChatChannelRepository channelRepository,
                       BotChatRepository botChatRepository,
                       ExternalSessionRepository sessionRepository) {
        this.channelRepository = channelRepository;
        this.botChatRepository = botChatRepository;
        this.sessionRepository = sessionRepository;
    }

    public ChatMessage sendMessage(final ChatChannelType type,
                                   String referenceId,
                                   final String senderId,
                                   final String content,
                                   final Jwt token) {
        if (type == ChatChannelType.AI) referenceId = senderId;

        if (type == ChatChannelType.LOBBY) {
            boolean allowed = sessionRepository.isPlayerInLobby(senderId, referenceId, token.getTokenValue());
            if (!allowed) {
                throw new NotInLobbyException(senderId, referenceId);
            }
        }

        String finalReferenceId = referenceId;
        ChatChannel channel = channelRepository
                .findByTypeAndReferenceId(type, referenceId)
                .orElseGet(() -> ChatChannel.createNew(type, finalReferenceId));

        ChatMessage userMessage = channel.postUserMessage(senderId, content);
        channelRepository.save(channel);

        if (type == ChatChannelType.LOBBY) return userMessage;

        AIResponse aiResponse = botChatRepository.askBot(
                "platform-chatbot-v1",
                senderId,
                referenceId,
                content
        );

        ChatMessage aiMessage = channel.postAIMessage(aiResponse.model(), aiResponse.answer());
        channelRepository.save(channel);

        return aiMessage;
    }

    public List<ChatMessage> getMessages(final ChatChannelType type,
                                         String referenceId,
                                         final Instant since,
                                         final String requesterId,
                                         final Jwt token) {

        if (type == ChatChannelType.AI) referenceId = requesterId;

        if (type == ChatChannelType.LOBBY) {
            if (!sessionRepository.isPlayerInLobby(requesterId, referenceId, token.getTokenValue())) {
                throw new NotInLobbyException(requesterId, referenceId);
            }
        }

        String finalReferenceId = referenceId;
        ChatChannel channel = channelRepository
                .findByTypeAndReferenceId(type, referenceId)
                .orElseThrow(() -> new ChatChannelNotFoundException(type, finalReferenceId));

        if (since == null) return channel.getMessages();
        return channel.getMessagesSince(since);
    }

//    public List<ChatMessage> getMessages(ChatChannelType type,
//                                         String referenceId,
//                                         String requesterId) {
//        return getMessages(type, referenceId, null, requesterId);
//    }

    public ChatChannel createChannel(ChatChannelType type, String referenceId) {
        if (type == ChatChannelType.AI) {
            throw new CantAutoCreateBotChannel("AI channels cannot be manually created");
        }

        ChatChannel existing = channelRepository
                .findByTypeAndReferenceId(type, referenceId)
                .orElse(null);

        if (existing != null) return existing;

        ChatChannel channel = ChatChannel.createNew(type, referenceId);
        channelRepository.save(channel);
        return channel;
    }
}