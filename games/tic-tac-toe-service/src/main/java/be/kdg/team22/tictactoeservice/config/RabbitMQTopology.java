package be.kdg.team22.tictactoeservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopology {
    public static final String EXCHANGE_GAMEPLAY = "exchange.gameplay";

    public static final String ROUTING_TICTACTOE_GAMEPLAY = "game.tictactoe.achievement";
    public static final String ROUTING_GAME_ENDED = "game.ended";

    public static final String QUEUE_TICTACTOE_EVENTS = "queue.tictactoe.events";

    @Bean
    TopicExchange gameplayExchange() {
        return new TopicExchange(EXCHANGE_GAMEPLAY, true, false);
    }

    @Bean
    Queue tictactoeQueue() {
        return QueueBuilder.durable(QUEUE_TICTACTOE_EVENTS).build();
    }

    @Bean
    Binding tictactoeAchievementBinding() {
        return BindingBuilder.bind(tictactoeQueue())
                .to(gameplayExchange())
                .with(ROUTING_TICTACTOE_GAMEPLAY);
    }
}