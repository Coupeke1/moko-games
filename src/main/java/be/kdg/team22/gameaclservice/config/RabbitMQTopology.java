package be.kdg.team22.gameaclservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopology {
    // Chess RabbitMQ - Incoming Exchanges
    public static final String CHESS_EXCHANGE = "gameExchange";

    // Chess RabbitMQ - Queues
    public static final String CHESS_ACHIEVEMENTS_QUEUE = "chess.achievements.queue";
    public static final String CHESS_GAME_ENDED_QUEUE = "chess.game.ended.queue";

    // Routing Keys
    public static final String ACHIEVEMENT_ROUTING_KEY = "achievement.acquired";
    public static final String GAME_ENDED_ROUTING_KEY = "game.ended";

    // Platform RabbitMQ - Outgoing Exchange
    public static final String PLATFORM_EXCHANGE = "exchange.gameplay";

    @Bean
    public TopicExchange chessExchange() {
        return new TopicExchange(CHESS_EXCHANGE, true, false);
    }

    @Bean
    public Queue chessAchievementQueue() {
        return new Queue(CHESS_ACHIEVEMENTS_QUEUE, true);
    }

    @Bean
    public Queue chessGameEndedQueue() {
        return new Queue(CHESS_GAME_ENDED_QUEUE, true);
    }

    @Bean
    public Binding chessAchievementBinding(Queue chessAchievementQueue, TopicExchange chessExchange) {
        return BindingBuilder.bind(chessAchievementQueue)
                .to(chessExchange)
                .with(ACHIEVEMENT_ROUTING_KEY);
    }

    @Bean
    public Binding chessGameEndedBinding(Queue chessGameEndedQueue, TopicExchange chessExchange) {
        return BindingBuilder.bind(chessGameEndedQueue)
                .to(chessExchange)
                .with(GAME_ENDED_ROUTING_KEY);
    }

    @Bean
    public SimpleMessageListenerContainer chessListenerContainer(
            @Qualifier("chessConnectionFactory") ConnectionFactory chessConnectionFactory,
            @Qualifier("platformRabbitTemplate") RabbitTemplate platformRabbitTemplate
    ) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(chessConnectionFactory);
        container.setQueues(chessAchievementQueue(), chessGameEndedQueue());
        container.setMissingQueuesFatal(false);

        MessageListenerAdapter adapter = new MessageListenerAdapter(
                new ChessEventListener(platformRabbitTemplate),
                "handleMessage"
        );
        adapter.setDefaultListenerMethod("handleMessage");
        adapter.setMessageConverter(new Jackson2JsonMessageConverter());

        container.setMessageListener(adapter);
        return container;
    }

    @Bean
    public TopicExchange platformExchange() {
        return new TopicExchange(PLATFORM_EXCHANGE, true, false);
    }

    @Bean(name = "platformRabbitTemplate")
    public RabbitTemplate platformRabbitTemplate(@Qualifier("platformConnectionFactory") ConnectionFactory platformConnectionFactory) {
        RabbitTemplate template = new RabbitTemplate(platformConnectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }
}