package be.kdg.team22.communicationservice.application.chat;

import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.channel.Channel;
import be.kdg.team22.communicationservice.infrastructure.messaging.ChatEventPublisher;
import be.kdg.team22.communicationservice.infrastructure.user.ExternalUserRepository;
import be.kdg.team22.communicationservice.infrastructure.user.ProfileResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ChatService {
    private final ChatEventPublisher chatEventPublisher;
    private final ExternalUserRepository userRepository;

    public ChatService(final ChatEventPublisher chatEventPublisher, final ExternalUserRepository userRepository) {
        this.chatEventPublisher = chatEventPublisher;
        this.userRepository = userRepository;
    }

    public void publishMessage(final UserId senderId, final UserId recipientId, final String content, final Channel channel) {
        ProfileResponse senderProfile = userRepository.getProfile(senderId.value());
        String messagePreview = content.length() > 50 ? content.substring(0, 47) + "..." : content;

        chatEventPublisher.publishDirectMessage(senderId.value(), senderProfile.username(), recipientId.value(), messagePreview, channel.id().value());
    }
}