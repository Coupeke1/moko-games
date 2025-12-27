package be.kdg.team22.communicationservice.infrastructure.chat;

import be.kdg.team22.communicationservice.application.chat.ChannelService;
import be.kdg.team22.communicationservice.config.RabbitMQTopology;
import be.kdg.team22.communicationservice.domain.chat.UserId;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChatListener {
    private final RabbitTemplate template;
    private final ChannelService service;

    public ChatListener(final RabbitTemplate template, final ChannelService service) {
        this.template = template;
        this.service = service;
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_SUBSCRIBED_SOCKET)
    public void subscribeListener(final SubscribeMessage message) {
        UserId userId = UserId.from(message.userId());
        System.out.printf("    %s sent %s", userId.value(), message.destination());
    }
}
