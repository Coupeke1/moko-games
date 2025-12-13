package be.kdg.team22.userservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopology {

    public static final String EXCHANGE_GAMEPLAY = "exchange.gameplay";
    public static final String QUEUE_USER_GAMEPLAY = "queue.user.gameplay";

    public static final String QUEUE_GAMES_PURCHASED = "queue.user-service.games-purchased";

    public static final String EXCHANGE_ACHIEVEMENTS = "exchange.achievements";

    @Bean
    Queue userGameplayQueue() {
        return QueueBuilder.durable(QUEUE_USER_GAMEPLAY).build();
    }

    @Bean
    Binding gameplayBinding() {
        return BindingBuilder.bind(userGameplayQueue())
                .to(new TopicExchange(EXCHANGE_GAMEPLAY))
                .with("game.#.achievements");
    }

    @Bean
    TopicExchange achievementsExchange() {
        return new TopicExchange(EXCHANGE_ACHIEVEMENTS, true, false);
    }
}