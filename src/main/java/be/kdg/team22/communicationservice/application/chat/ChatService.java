package be.kdg.team22.communicationservice.application.chat;

import be.kdg.team22.communicationservice.domain.chat.*;
import be.kdg.team22.communicationservice.domain.chat.bot.BotChatRepository;
import be.kdg.team22.communicationservice.domain.chat.bot.BotResponse;
import be.kdg.team22.communicationservice.domain.chat.exceptions.CantAutoCreateBotChannel;
import be.kdg.team22.communicationservice.domain.chat.exceptions.ChatChannelNotFoundException;
import be.kdg.team22.communicationservice.domain.chat.exceptions.NotFriendsException;
import be.kdg.team22.communicationservice.domain.chat.exceptions.NotInLobbyException;
import be.kdg.team22.communicationservice.infrastructure.lobby.ExternalSessionRepository;
import be.kdg.team22.communicationservice.infrastructure.messaging.ChatEventPublisher;
import be.kdg.team22.communicationservice.infrastructure.social.ExternalSocialRepository;
import be.kdg.team22.communicationservice.infrastructure.user.ExternalUserRepository;
import be.kdg.team22.communicationservice.infrastructure.user.ProfileResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ChatService {
    private final ChatChannelRepository channelRepository;
    private final BotChatRepository botChatRepository;
    private final ExternalSessionRepository sessionRepository;
    private final ExternalSocialRepository socialRepository;
    private final ChatEventPublisher chatEventPublisher;
    private final ExternalUserRepository userRepository;

    public ChatService(final ChatChannelRepository channelRepository,
                       final BotChatRepository botChatRepository,
                       final ExternalSessionRepository sessionRepository,
                       final ExternalSocialRepository socialRepository,
                       final ChatEventPublisher chatEventPublisher,
                       final ExternalUserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.botChatRepository = botChatRepository;
        this.sessionRepository = sessionRepository;
        this.socialRepository = socialRepository;
        this.chatEventPublisher = chatEventPublisher;
        this.userRepository = userRepository;
    }

    public ChatMessage sendMessage(final ChatChannelType channelType,
                                   final String referenceId,
                                   final String senderId,
                                   final String content,
                                   final Jwt token) {
        String resolvedReferenceId = resolveReferenceId(channelType, referenceId, senderId, token);

        Channel channel = channelRepository
                .findByTypeAndReferenceId(channelType, resolvedReferenceId)
                .orElseGet(() -> Channel.createNew(channelType, resolvedReferenceId));

        ChatMessage userMessage = channel.postUserMessage(senderId, content);
        channelRepository.save(channel);

        if (channelType == ChatChannelType.FRIENDS) {
            publishFriendMessageEvent(senderId, referenceId, content, channel);
            return userMessage;
        }

        if (channelType == ChatChannelType.LOBBY) return userMessage;

        BotResponse botResponse = botChatRepository.askBot(
                "platform-chatbot-v1",
                senderId,
                resolvedReferenceId,
                content
        );

        ChatMessage aiMessage = channel.postBotMessage(botResponse.model(), botResponse.answer());
        channelRepository.save(channel);

        return aiMessage;
    }

    private void publishFriendMessageEvent(final String senderId,
                                           final String recipientId,
                                           final String content,
                                           final Channel channel) {
        ProfileResponse senderProfile = userRepository.getProfile(UUID.fromString(senderId));
                String messagePreview = content.length() > 50 ? content.substring(0, 47) + "..." : content;

        chatEventPublisher.publishDirectMessage(
                UUID.fromString(senderId),
                senderProfile.username(),
                UUID.fromString(recipientId),
                messagePreview,
                channel.getId().value()
        );
    }

    public List<ChatMessage> getMessages(final ChatChannelType channelType,
                                         String referenceId,
                                         final Instant since,
                                         final String requesterId,
                                         final Jwt token) {
        referenceId = resolveReferenceId(channelType, referenceId, requesterId, token);

        String finalReferenceId = referenceId;
        Channel channel = channelRepository
                .findByTypeAndReferenceId(channelType, referenceId)
                .orElseThrow(() -> new ChatChannelNotFoundException(channelType, finalReferenceId));

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

    private String resolveReferenceId(final ChatChannelType channelType,
                                      final String referenceId,
                                      final String userId,
                                      final Jwt token) {
        return switch (channelType) {
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