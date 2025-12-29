package be.kdg.team22.communicationservice.infrastructure.chat;

import be.kdg.team22.communicationservice.api.chat.models.MessageModel;
import be.kdg.team22.communicationservice.config.RabbitMQTopology;
import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.channel.Channel;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.BotReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.LobbyReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.PrivateReferenceType;
import be.kdg.team22.communicationservice.domain.chat.message.Message;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import be.kdg.team22.communicationservice.infrastructure.lobby.ExternalLobbyRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatPublisher {
    private final RabbitTemplate template;
    private final ExternalLobbyRepository repository;

    public ChatPublisher(final RabbitTemplate template, final ExternalLobbyRepository repository) {
        this.template = template;
        this.repository = repository;
    }

    public void publishToPlayers(final Channel channel, final Message message) {
        MessageModel model = MessageModel.from(message);
        String queue = String.format("chat-%s", message.channelId());

        for (UserId userId : getUsers(channel)) {
            ChatMessage chatMessage = new ChatMessage(userId.value(), queue, model);
            template.convertAndSend(RabbitMQTopology.EXCHANGE_USER_SOCKET, "user.message", chatMessage);
        }
    }

    private UserId[] getUsers(final Channel channel) {
        switch (channel.referenceType().type()) {
            case ChannelType.FRIENDS -> {
                PrivateReferenceType referenceType = (PrivateReferenceType) channel.referenceType();
                return new UserId[]{referenceType.userId(), referenceType.otherUserId()};
            }

            case ChannelType.LOBBY -> {
                LobbyReferenceType referenceType = (LobbyReferenceType) channel.referenceType();
                List<PlayerId> players = repository.findPlayers(referenceType.lobbyId().value());
                
                return players.stream().map(player -> new UserId(player.value())).toList().toArray(new UserId[0]);
            }

            case ChannelType.BOT -> {
                BotReferenceType referenceType = (BotReferenceType) channel.referenceType();
                return new UserId[]{referenceType.userId()};
            }
        }

        return new UserId[0];
    }
}