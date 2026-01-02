package be.kdg.team22.gameaclservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopology {
    // CHESS SERVICE
    public static final String EXCHANGE_CHESS = "gameExchange";

    // PLATFORM
    public static final String EXCHANGE_PLATFORM_GAMEPLAY = "exchange.gameplay";

    // CHESS SERVICE - Routing Keys
    public static final String ROUTING_KEY_CHESS_ACHIEVEMENT_ACQUIRED = "achievement.acquired";

    // PLATFORM - Routing Keys
    public static final String ROUTING_KEY_PLATFORM_ACHIEVEMENT_ACQUIRED = "game.chess.achievement";
    public static final String ROUTING_KEY_PLATFORM_GAME_ENDED = "game.ended";

    // CHESS SERVICE - Queues
    public static final String QUEUE_CHESS_EVENTS = "queue.acl.chess-events";

    // CHESS SERVICE
    @Bean
    TopicExchange chessExchange() {
        return new TopicExchange(EXCHANGE_CHESS, true, false);
    }

    @Bean
    Queue chessQueue() {
        return QueueBuilder.durable(QUEUE_CHESS_EVENTS).build();
    }

    @Bean
    Binding chessBinding() {
        return BindingBuilder.bind(chessQueue())
                .to(chessExchange())
                .with("game.*");
    }

    // PLATFORM
    @Bean
    TopicExchange gameplayExchange() {
        return new TopicExchange(EXCHANGE_PLATFORM_GAMEPLAY, true, false);
    }
}