package be.kdg.team22.communicationservice.application.chat;

import be.kdg.team22.communicationservice.domain.chat.Channel;
import be.kdg.team22.communicationservice.domain.chat.ChatChannelRepository;
import be.kdg.team22.communicationservice.domain.chat.ChatChannelType;
import be.kdg.team22.communicationservice.domain.chat.ChatMessage;
import be.kdg.team22.communicationservice.domain.chat.FriendshipId;
import be.kdg.team22.communicationservice.domain.chat.bot.BotResponse;
import be.kdg.team22.communicationservice.domain.chat.bot.BotChatRepository;
import be.kdg.team22.communicationservice.domain.chat.exceptions.CantAutoCreateBotChannel;
import be.kdg.team22.communicationservice.domain.chat.exceptions.ChatChannelNotFoundException;
import be.kdg.team22.communicationservice.domain.chat.exceptions.NotFriendsException;
import be.kdg.team22.communicationservice.domain.chat.exceptions.NotInLobbyException;
import be.kdg.team22.communicationservice.infrastructure.lobby.ExternalSessionRepository;
import be.kdg.team22.communicationservice.infrastructure.social.ExternalSocialRepository;
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
    private final ExternalSocialRepository socialRepository;

    public ChatService(final ChatChannelRepository channelRepository,
                       final BotChatRepository botChatRepository,
                       final ExternalSessionRepository sessionRepository,
                       final ExternalSocialRepository socialRepository) {
        this.channelRepository = channelRepository;
        this.botChatRepository = botChatRepository;
        this.sessionRepository = sessionRepository;
        this.socialRepository = socialRepository;
    }

    public ChatMessage sendMessage(final ChatChannelType type,
                                   String referenceId,
                                   final String senderId,
                                   final String content,
                                   final Jwt token) {
        referenceId = resolveReferenceId(type, referenceId, senderId, token);

        String finalReferenceId = referenceId;
        Channel channel = channelRepository
                .findByTypeAndReferenceId(type, referenceId)
                .orElseGet(() -> Channel.createNew(type, finalReferenceId));

        ChatMessage userMessage = channel.postUserMessage(senderId, content);
        channelRepository.save(channel);

        if (type == ChatChannelType.LOBBY || type == ChatChannelType.FRIENDS) return userMessage;

        BotResponse botResponse = botChatRepository.askBot(
                "platform-chatbot-v1",
                senderId,
                referenceId,
                content
        );

        ChatMessage aiMessage = channel.postBotMessage(botResponse.model(), botResponse.answer());
        channelRepository.save(channel);

        return aiMessage;
    }

    public List<ChatMessage> getMessages(final ChatChannelType type,
                                         String referenceId,
                                         final Instant since,
                                         final String requesterId,
                                         final Jwt token) {
        referenceId = resolveReferenceId(type, referenceId, requesterId, token);

        String finalReferenceId = referenceId;
        Channel channel = channelRepository
                .findByTypeAndReferenceId(type, referenceId)
                .orElseThrow(() -> new ChatChannelNotFoundException(type, finalReferenceId));

        if (since == null) return channel.getMessages();
        return channel.getMessagesSince(since);
    }

    public Channel createChannel(final ChatChannelType type, final String referenceId) {
        if (type == ChatChannelType.BOT) {
            throw new CantAutoCreateBotChannel("BOT channels cannot be manually created");
        }

        Channel existing = channelRepository
                .findByTypeAndReferenceId(type, referenceId)
                .orElse(null);

        if (existing != null) return existing;

        Channel channel = Channel.createNew(type, referenceId);
        channelRepository.save(channel);
        return channel;
    }

    private String resolveReferenceId(final ChatChannelType type,
                                      final String referenceId,
                                      final String userId,
                                      final Jwt token) {
        return switch (type) {
            case BOT -> userId;
            case LOBBY -> {
                if (!sessionRepository.isPlayerInLobby(userId, referenceId, token.getTokenValue())) {
                    throw new NotInLobbyException(userId, referenceId);
                }
                yield referenceId;
            }
            case FRIENDS -> {
                FriendshipId friendshipId = FriendshipId.create(referenceId);
                if (!socialRepository.areFriends(userId, friendshipId.value().toString(), token.getTokenValue())) {
                    throw new NotFriendsException(userId, friendshipId.value().toString());
                }
                yield FriendshipId.toReferenceId(userId, friendshipId.value().toString());
            }
        };
    }
}