package be.kdg.team22.sessionservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQTopology {
    public static final String ROUTING_KEY_LOBBY_JOINED = "session.lobby.joined";
    public static final String ROUTING_KEY_LOBBY_INVITE = "session.lobby.invite";

    public static final String EXCHANGE_SESSION = "exchange.session";
    public static final String EXCHANGE_GAMEPLAY = "exchange.gameplay";
    public static final String EXCHANGE_USER_SOCKET = "user.direct.exchange";

    public static final String ROUTING_KEY_GAME_ENDED = "game.ended";

    public static final String QUEUE_GAME_ENDED = "queue.session.game.ended";
    public static final String QUEUE_USER_SOCKET = "user.socket.queue";

    @Bean
    TopicExchange sessionExchange() {
        return new TopicExchange(EXCHANGE_SESSION, true, false);
    }

    @Bean
    TopicExchange gameplayExchange() {
        return new TopicExchange(EXCHANGE_GAMEPLAY, true, false);
    }

    @Bean
    TopicExchange userSocketExchange() {
        return new TopicExchange(EXCHANGE_USER_SOCKET, true, false);
    }

    @Bean
    Queue gameEndedQueue() {
        return QueueBuilder.durable(QUEUE_GAME_ENDED).build();
    }

    @Bean
    Queue userSocketQueue() {
        return QueueBuilder.durable(QUEUE_USER_SOCKET).build();
    }

    @Bean
    Binding gameEndedBinding() {
        return BindingBuilder.bind(gameEndedQueue()).to(gameplayExchange()).with(ROUTING_KEY_GAME_ENDED);
    }

    @Bean
    Binding bindUserSocketMessage() {
        return BindingBuilder.bind(userSocketQueue()).to(userSocketExchange()).with("user.message");
    }
}