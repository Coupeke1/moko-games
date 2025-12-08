package be.kdg.team22.checkersservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopology {
    public static final String EXCHANGE_GAMEPLAY = "exchange.gameplay";

    public static final String ROUTING_CHECKERS_DRAW = "checkers.draw";
    public static final String ROUTING_CHECKERS_LOST = "checkers.lost";
    public static final String ROUTING_CHECKERS_WON = "checkers.won";
    public static final String ROUTING_CHECKERS_PROMOTION = "checkers.promotion";
    public static final String ROUTING_CHECKERS_MULTICAPTURE = "checkers.multicapture";
    public static final String ROUTING_CHECKERS_THREEKINGS = "checkers.threekings";

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
    Binding checkersDrawBinding() {
        return BindingBuilder.bind(checkersQueue())
                .to(gameplayExchange())
                .with(ROUTING_CHECKERS_DRAW);
    }

    @Bean
    Binding checkersLostBinding() {
        return BindingBuilder.bind(checkersQueue())
                .to(gameplayExchange())
                .with(ROUTING_CHECKERS_LOST);
    }

    @Bean
    Binding checkersWonBinding() {
        return BindingBuilder.bind(checkersQueue())
                .to(gameplayExchange())
                .with(ROUTING_CHECKERS_WON);
    }

    @Bean
    Binding checkersPromotionBinding() {
        return BindingBuilder.bind(checkersQueue())
                .to(gameplayExchange())
                .with(ROUTING_CHECKERS_PROMOTION);
    }

    @Bean
    Binding checkersMulticaptureBinding() {
        return BindingBuilder.bind(checkersQueue())
                .to(gameplayExchange())
                .with(ROUTING_CHECKERS_MULTICAPTURE);
    }

    @Bean
    Binding checkersThreeKingsBinding() {
        return BindingBuilder.bind(checkersQueue())
                .to(gameplayExchange())
                .with(ROUTING_CHECKERS_THREEKINGS);
    }
}