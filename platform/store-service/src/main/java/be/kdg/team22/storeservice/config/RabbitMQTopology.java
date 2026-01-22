package be.kdg.team22.storeservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopology {
    public static final String EXCHANGE_STORE = "exchange.store";
    public static final String Q_ORDER_COMPLETED = "queue.notifications.order-completed";
    public static final String ROUTING_KEY_ORDER_COMPLETED = "store.order.completed";
    public static final String Q_GAMES_PURCHASED = "queue.user-service.games-purchased";
    public static final String ROUTING_KEY_GAMES_PURCHASED = "store.games.purchased";

    @Bean
    TopicExchange storeExchange() {
        return new TopicExchange(EXCHANGE_STORE, true, false);
    }

    @Bean
    Queue orderCompletedQueue() {
        return QueueBuilder.durable(Q_ORDER_COMPLETED).build();
    }

    @Bean
    Binding bindOrderCompleted() {
        return BindingBuilder.bind(orderCompletedQueue())
                .to(storeExchange()).with(ROUTING_KEY_ORDER_COMPLETED);
    }

    @Bean
    Queue gamesPurchasedQueue() {
        return QueueBuilder.durable(Q_GAMES_PURCHASED).build();
    }

    @Bean
    Binding bindGamesPurchased() {
        return BindingBuilder.bind(gamesPurchasedQueue())
                .to(storeExchange()).with(ROUTING_KEY_GAMES_PURCHASED);
    }
}