package be.kdg.team22.checkersservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopology {
    public static final String EXCHANGE_GAMEPLAY = "exchange.gameplay";

    public static final String ROUTING_CHECKERS_GAMEPLAY = "game.checkers";

    public static final String QUEUE_CHECKERS_EVENTS = "queue.checkers.events";

    @Bean
    TopicExchange gameplayExchange() {
        return new TopicExchange(EXCHANGE_GAMEPLAY, true, false);
    }

    @Bean
    Queue checkersQueue() {
        return QueueBuilder.durable(QUEUE_CHECKERS_EVENTS).build();
    }

    @Bean
    Binding checkersAchievementBinding() {
        return BindingBuilder.bind(checkersQueue())
                .to(gameplayExchange())
                .with(ROUTING_CHECKERS_GAMEPLAY);
    }
}