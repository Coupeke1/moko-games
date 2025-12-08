package be.kdg.team22.socialservice.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopology {
    public static final String EXCHANGE_SOCIAL = "exchange.social";

    public static final String ROUTING_KEY_FRIEND_REQUEST_RECEIVED = "social.friend-request.received";
    public static final String ROUTING_KEY_FRIEND_REQUEST_ACCEPTED = "social.friend-request.accepted";

    @Bean
    TopicExchange socialExchange() {
        return new TopicExchange(EXCHANGE_SOCIAL, true, false);
    }
}

