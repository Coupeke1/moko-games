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
    public static final String ROUTING_KEY_CHESS_GAME_REGISTERED = "game.registered";
    public static final String ROUTING_KEY_CHESS_GAME_ENDED = "game.ended";

    // PLATFORM - Routing Keys
    public static final String ROUTING_KEY_PLATFORM_ACHIEVEMENT_ACQUIRED = "game.chess.achievement";
    public static final String ROUTING_KEY_PLATFORM_GAME_ENDED = "game.ended";

    // CHESS SERVICE - Queues
    public static final String QUEUE_CHESS_EVENTS = "queue.acl.chess-events";
    public static final String QUEUE_CHESS_EVENTS_REGISTERED = "queue.acl.chess-events.registered";
    public static final String QUEUE_CHESS_EVENTS_ACHIEVEMENT = "queue.acl.chess-events.achievement";
    public static final String QUEUE_CHESS_EVENTS_ENDED = "queue.acl.chess-events.ended";

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
    Queue chessQueueRegistered() {
        return QueueBuilder.durable(QUEUE_CHESS_EVENTS_REGISTERED).build();
    }

    @Bean
    Queue chessQueueAchievement() {
        return QueueBuilder.durable(QUEUE_CHESS_EVENTS_ACHIEVEMENT).build();
    }

    @Bean
    Queue chessQueueEnded() {
        return QueueBuilder.durable(QUEUE_CHESS_EVENTS_ENDED).build();
    }

    @Bean
    Binding chessBinding() {
        return BindingBuilder.bind(chessQueue())
                .to(chessExchange())
                .with("game.*");
    }

    @Bean
    Binding registeredBinding() {
        return BindingBuilder.bind(chessQueueRegistered())
                .to(chessExchange())
                .with(ROUTING_KEY_CHESS_GAME_REGISTERED);
    }

    @Bean
    Binding achievementBinding() {
        return BindingBuilder.bind(chessQueueAchievement())
                .to(chessExchange())
                .with(ROUTING_KEY_CHESS_ACHIEVEMENT_ACQUIRED);
    }

    @Bean
    Binding endedBinding() {
        return BindingBuilder.bind(chessQueueEnded())
                .to(chessExchange())
                .with(ROUTING_KEY_CHESS_GAME_ENDED);
    }

    // PLATFORM
    @Bean
    TopicExchange gameplayExchange() {
        return new TopicExchange(EXCHANGE_PLATFORM_GAMEPLAY, true, false);
    }
}