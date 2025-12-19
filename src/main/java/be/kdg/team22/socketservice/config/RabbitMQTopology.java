package be.kdg.team22.socketservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopology {
    public static final String EXCHANGE_USER_SOCKET = "user.direct.exchange";
    public static final String EXCHANGE_SUBSCRIBED_SOCKET = "socket.subscription.exchange";
    public static final String QUEUE_USER_SOCKET = "user.socket.queue";

    @Bean
    TopicExchange userSocketExchange() {
        return new TopicExchange(EXCHANGE_USER_SOCKET, true, false);
    }

    @Bean
    TopicExchange subscribedSocketExchange() {
        return new TopicExchange(EXCHANGE_SUBSCRIBED_SOCKET, true, false);
    }

    @Bean
    Queue userSocketQueue() {
        return QueueBuilder.durable(QUEUE_USER_SOCKET).build();
    }

    @Bean
    Binding bindUserSocketMessage() {
        return BindingBuilder.bind(userSocketQueue()).to(userSocketExchange()).with("user.message");
    }
}
