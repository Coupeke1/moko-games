package be.kdg.team22.communicationservice.application.chat;

import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.channel.*;
import be.kdg.team22.communicationservice.domain.chat.channel.exceptions.InvalidChannelException;
import be.kdg.team22.communicationservice.domain.chat.channel.exceptions.NoAccessException;
import be.kdg.team22.communicationservice.domain.chat.channel.type.BotReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.LobbyReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.PrivateReferenceType;
import be.kdg.team22.communicationservice.domain.chat.message.Message;
import be.kdg.team22.communicationservice.infrastructure.lobby.ExternalLobbyRepository;
import be.kdg.team22.communicationservice.infrastructure.messaging.ChatEventPublisher;
import be.kdg.team22.communicationservice.infrastructure.user.ExternalUserRepository;
import be.kdg.team22.communicationservice.infrastructure.user.ProfileResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class ChatService {
    private final ChannelService channelService;
    private final ChannelRepository channelRepository;
    private final ChatEventPublisher chatEventPublisher;
    private final ExternalUserRepository userRepository;
    private final ExternalLobbyRepository sessionRepository;
    private final ChatPublisherService publisher;

    public ChatService(final ChannelService channelService, final ChannelRepository channelRepository, final ChatEventPublisher chatEventPublisher, final ExternalUserRepository userRepository, final ExternalLobbyRepository sessionRepository, final ChatPublisherService publisher) {
        this.channelService = channelService;
        this.channelRepository = channelRepository;
        this.chatEventPublisher = chatEventPublisher;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.publisher = publisher;
    }

    public Message sendLobbyMessage(final ChannelId channelId, final UserId userId, final String content) {
        Channel channel = channelService.getChannel(channelId);
        if (!channel.referenceType().type().equals(ChannelType.LOBBY))
            throw new InvalidChannelException(channelId, ChannelType.LOBBY);

        Message message = channel.send(userId, content);
        publisher.saveAndPublish(channel, message);
        return message;
    }

    public List<Message> getMessages(final ChannelId channelId, final UserId userId, final Instant since, final Jwt token) {
        Channel channel = channelService.getChannel(channelId);
        checkForAccess(channel, userId, token);
        return since == null ? channel.getMessages() : channel.getMessagesSince(since);
    }

    public Message sendPrivateMessage(final ChannelId channelId, final UserId userId, final String content, final Jwt token) {
        Channel channel = channelService.getChannel(channelId);
        if (!channel.referenceType().type().equals(ChannelType.FRIENDS))
            throw new InvalidChannelException(channelId, ChannelType.FRIENDS);

        checkForAccess(channel, userId, token);
        PrivateReferenceType referenceType = (PrivateReferenceType) channel.referenceType();
        UserId friendId = referenceType.getOtherUser(userId);

        Message message = channel.send(userId, content);

        publishMessage(userId, friendId, content, channel);
        publisher.saveAndPublish(channel, message);

        return message;
    }

    private void publishMessage(final UserId senderId, final UserId recipientId, final String content, final Channel channel) {
        ProfileResponse senderProfile = userRepository.getProfile(senderId.value());
        String messagePreview = content.length() > 50 ? content.substring(0, 47) + "..." : content;

        chatEventPublisher.publishDirectMessage(senderId.value(), senderProfile.username(), recipientId.value(), messagePreview, channel.id().value());
    }

    private void checkForAccess(final Channel channel, final UserId userId, final Jwt token) {
        switch (channel.referenceType().type()) {
            case ChannelType.FRIENDS -> {
                PrivateReferenceType referenceType = (PrivateReferenceType) channel.referenceType();
                if (referenceType.hasUser(userId))
                    return;

                throw new NoAccessException(userId, channel.id());
            }

            case ChannelType.LOBBY -> {
                LobbyReferenceType referenceType = (LobbyReferenceType) channel.referenceType();

                LobbyId lobbyId = referenceType.lobbyId();
                if (sessionRepository.isPlayerInLobby(userId.value(), lobbyId.value(), token))
                    return;

                throw new NoAccessException(userId, channel.id());
            }

            case ChannelType.BOT -> {
                BotReferenceType referenceType = (BotReferenceType) channel.referenceType();
                if (referenceType.userId().equals(userId))
                    return;

                throw new NoAccessException(userId, channel.id());
            }
        }
    }
}