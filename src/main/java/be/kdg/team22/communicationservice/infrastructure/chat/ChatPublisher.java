package be.kdg.team22.communicationservice.infrastructure.chat;

import be.kdg.team22.communicationservice.api.chat.models.MessageModel;
import be.kdg.team22.communicationservice.config.RabbitMQTopology;
import be.kdg.team22.communicationservice.domain.chat.channel.Channel;
import be.kdg.team22.communicationservice.domain.chat.message.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChatPublisher {
    private final RabbitTemplate template;

    public ChatPublisher(final RabbitTemplate template) {
        this.template = template;
    }

    public void publishToPlayers(final Channel channel, final Message message) {
        MessageModel model = MessageModel.from(message);
        String queue = String.format("chat-%s", message.channelId());

        // TODO: get all users that need to get the message (like in ChatService at `checkForAccess`)

        template.convertAndSend(RabbitMQTopology.EXCHANGE_USER_SOCKET, "user.message", new ChatMessage(message.userId().value(), queue, model));
    }
}