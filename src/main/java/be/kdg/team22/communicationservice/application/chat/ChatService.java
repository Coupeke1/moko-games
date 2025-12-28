package be.kdg.team22.communicationservice.application.chat;

import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.channel.Channel;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelRepository;
import be.kdg.team22.communicationservice.domain.chat.channel.LobbyId;
import be.kdg.team22.communicationservice.domain.chat.message.Message;
import be.kdg.team22.communicationservice.infrastructure.messaging.ChatEventPublisher;
import be.kdg.team22.communicationservice.infrastructure.user.ExternalUserRepository;
import be.kdg.team22.communicationservice.infrastructure.user.ProfileResponse;
import jakarta.transaction.Transactional;
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
    private final ChatPublisherService publisher;

    public ChatService(final ChannelService channelService, final ChannelRepository channelRepository, final ChatEventPublisher chatEventPublisher, final ExternalUserRepository userRepository, final ChatPublisherService publisher) {
        this.channelService = channelService;
        this.channelRepository = channelRepository;
        this.chatEventPublisher = chatEventPublisher;
        this.userRepository = userRepository;
        this.publisher = publisher;
    }

    public Message sendLobbyMessage(final LobbyId lobbyId, final UserId senderId, final String content) {
        Channel channel = channelService.getOrCreateLobbyChannel(lobbyId);
        Message message = channel.send(senderId, content);
        channelRepository.save(channel);
        return message;
    }

    public List<Message> getLobbyMessages(final LobbyId lobbyId, final Instant since) {
        Channel channel = channelService.getOrCreateLobbyChannel(lobbyId);
        return since == null ? channel.getMessages() : channel.getMessagesSince(since);
    }

    public Message sendPrivateMessage(final UserId friendId, final UserId senderId, final String content) {
        Channel channel = channelService.getOrCreatePrivateChannel(friendId, senderId);
        Message message = channel.send(senderId, content);

        publishMessage(senderId, friendId, content, channel);
        publisher.saveAndPublish(channel, message);
        return message;
    }

    public List<Message> getPrivateMessages(final UserId userId, final UserId otherUserId, final Instant since) {
        Channel channel = channelService.getOrCreatePrivateChannel(userId, otherUserId);
        return since == null ? channel.getMessages() : channel.getMessagesSince(since);
    }

    private void publishMessage(final UserId senderId, final UserId recipientId, final String content, final Channel channel) {
        ProfileResponse senderProfile = userRepository.getProfile(senderId.value());
        String messagePreview = content.length() > 50 ? content.substring(0, 47) + "..." : content;

        chatEventPublisher.publishDirectMessage(senderId.value(), senderProfile.username(), recipientId.value(), messagePreview, channel.id().value());
    }
}