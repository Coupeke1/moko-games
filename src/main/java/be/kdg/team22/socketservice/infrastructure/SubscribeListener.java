package be.kdg.team22.socketservice.infrastructure;

import be.kdg.team22.socketservice.config.RabbitMQTopology;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.UUID;
import java.util.logging.Logger;

@Component
public class SubscribeListener {
    private final RabbitTemplate template;

    public SubscribeListener(RabbitTemplate template) {
        this.template = template;
    }

    @EventListener
    public void onSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        System.out.println("    RECEIVED SUBSCRIPTION");

        if (destination == null || accessor.getUser() == null)
            return;

        String user = accessor.getUser().getName();
        String key = String.format("socket.subscribe.%s", normalize(destination));
        String session = accessor.getSessionId();
        System.out.println("    SENDING SUBSCRIPTION");

        template.convertAndSend(RabbitMQTopology.EXCHANGE_SUBSCRIBED_SOCKET, key, new SubscribeMessage(UUID.fromString(user), destination, session));
    }

    private String normalize(String destination) {
        if (destination == null) return "unknown";
        return destination.replaceFirst("^/", "").replaceAll("/", ".").replaceAll("[^a-zA-Z0-9.]", "_");
    }
}