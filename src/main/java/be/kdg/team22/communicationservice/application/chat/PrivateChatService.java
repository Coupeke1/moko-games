package be.kdg.team22.communicationservice.application.chat;

import be.kdg.team22.communicationservice.application.chat.exceptions.InvalidException;
import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.channel.Channel;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelId;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelType;
import be.kdg.team22.communicationservice.domain.chat.channel.exceptions.InvalidChannelException;
import be.kdg.team22.communicationservice.domain.chat.channel.exceptions.NoAccessException;
import be.kdg.team22.communicationservice.domain.chat.channel.type.PrivateReferenceType;
import be.kdg.team22.communicationservice.domain.chat.message.Message;
import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class PrivateChatService {
    private final ChannelService channelService;
    private final ChatService chatService;
    private final ChatPublisherService publisher;

    public PrivateChatService(final ChannelService channelService, final ChatService chatService, final ChatPublisherService publisher) {
        this.channelService = channelService;
        this.chatService = chatService;
        this.publisher = publisher;
    }

    public List<Message> getMessages(final ChannelId channelId, final Instant since, final Jwt token) {
        Channel channel = channelService.getChannel(channelId);
        UserId userId = UserId.get(token);

        checkForAccess(channel, userId);
        return since == null ? channel.getMessages() : channel.getMessagesSince(since);
    }

    public Message sendMessage(final ChannelId channelId, final String content, final Jwt token) {
        Channel channel = channelService.getChannel(channelId);
        if (!channel.referenceType().type().equals(ChannelType.FRIENDS))
            throw new InvalidChannelException(channelId, ChannelType.FRIENDS);

        UserId userId = UserId.get(token);
        checkForAccess(channel, userId);

        PrivateReferenceType referenceType = (PrivateReferenceType) channel.referenceType();
        UserId friendId = referenceType.getOtherUser(userId);

        if (content == null || content.isEmpty())
            throw InvalidException.content();

        Message message = channel.send(userId, content);

        chatService.publishMessage(userId, friendId, content, channel);
        publisher.saveAndPublish(channel, message);
        return message;
    }

    private void checkType(final Channel channel) {
        if (channel.referenceType().type().equals(ChannelType.FRIENDS))
            return;

        throw new InvalidChannelException(channel.id(), ChannelType.FRIENDS);
    }

    private void checkForAccess(final Channel channel, final UserId userId) {
        PrivateReferenceType referenceType = (PrivateReferenceType) channel.referenceType();
        if (referenceType.hasUser(userId)) return;
        throw new NoAccessException(userId, channel.id());
    }
}
