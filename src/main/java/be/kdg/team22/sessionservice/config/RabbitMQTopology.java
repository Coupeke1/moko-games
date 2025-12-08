package be.kdg.team22.sessionservice.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQTopology {
    public static final String ROUTING_KEY_LOBBY_JOINED = "session.lobby.joined";
    public static final String ROUTING_KEY_LOBBY_INVITE = "session.lobby.invite";

    public static final String EXCHANGE_SESSION = "exchange.session";

    @Bean
    TopicExchange sessionExchange() {
        return new TopicExchange(EXCHANGE_SESSION, true, false);
    }
}