package be.kdg.team22.communicationservice.application.chat;

import be.kdg.team22.communicationservice.application.chat.exceptions.InvalidException;
import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.channel.Channel;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelId;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelType;
import be.kdg.team22.communicationservice.domain.chat.channel.LobbyId;
import be.kdg.team22.communicationservice.domain.chat.channel.exceptions.InvalidChannelException;
import be.kdg.team22.communicationservice.domain.chat.channel.exceptions.NoAccessException;
import be.kdg.team22.communicationservice.domain.chat.channel.type.LobbyReferenceType;
import be.kdg.team22.communicationservice.domain.chat.message.Message;
import be.kdg.team22.communicationservice.infrastructure.lobby.ExternalLobbyRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class LobbyChatService {
    private final ChannelService channelService;
    private final ChatPublisherService publisher;
    private final ExternalLobbyRepository sessionRepository;

    public LobbyChatService(final ChannelService channelService, final ChatPublisherService publisher, final ExternalLobbyRepository sessionRepository) {
        this.channelService = channelService;
        this.publisher = publisher;
        this.sessionRepository = sessionRepository;
    }

    public List<Message> getMessages(final ChannelId channelId, final Instant since, final Jwt token) {
        Channel channel = channelService.getChannel(channelId);
        checkType(channel);

        UserId userId = UserId.get(token);
        checkForAccess(channel, userId, token);

        return since == null ? channel.getMessages() : channel.getMessagesSince(since);
    }

    public Message sendMessage(final ChannelId channelId, final String content, final Jwt token) {
        Channel channel = channelService.getChannel(channelId);
        checkType(channel);

        UserId userId = UserId.get(token);
        checkForAccess(channel, userId, token);

        if (content == null || content.isEmpty())
            throw InvalidException.content();

        Message message = channel.send(userId, content);
        publisher.saveAndPublish(channel, message);
        return message;
    }

    private void checkType(final Channel channel) {
        if (channel.referenceType().type().equals(ChannelType.BOT))
            return;

        throw new InvalidChannelException(channel.id(), ChannelType.BOT);
    }

    private void checkForAccess(final Channel channel, final UserId userId, final Jwt token) {
        LobbyReferenceType referenceType = (LobbyReferenceType) channel.referenceType();

        LobbyId lobbyId = referenceType.lobbyId();
        if (sessionRepository.isPlayerInLobby(userId.value(), lobbyId.value(), token))
            return;

        throw new NoAccessException(userId, channel.id());
    }
}
