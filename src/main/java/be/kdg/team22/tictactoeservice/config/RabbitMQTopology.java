package be.kdg.team22.tictactoeservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopology {
    public static final String EXCHANGE_GAMEPLAY = "exchange.gameplay";

    public static final String ROUTING_TICTACTOE_WON = "tictactoe.won";
    public static final String ROUTING_TICTACTOE_DRAW = "tictactoe.draw";

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
    Binding tictactoeWonBinding() {
        return BindingBuilder.bind(tictactoeQueue())
                .to(gameplayExchange())
                .with(ROUTING_TICTACTOE_WON);
    }

    @Bean
    Binding tictactoeDrawBinding() {
        return BindingBuilder.bind(tictactoeQueue())
                .to(gameplayExchange())
                .with(ROUTING_TICTACTOE_DRAW);
    }
}