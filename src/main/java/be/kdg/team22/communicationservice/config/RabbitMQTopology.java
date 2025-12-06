package be.kdg.team22.communicationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopology {
    public static final String EXCHANGE_SOCIAL = "exchange.social";
    public static final String EXCHANGE_SESSION = "exchange.session";
    public static final String EXCHANGE_ACHIEVEMENTS = "exchange.achievements";
    public static final String EXCHANGE_STORE = "exchange.store";
    public static final String EXCHANGE_CHAT = "exchange.chat";

    public static final String Q_FRIEND_REQUEST_RECEIVED = "queue.notifications.friend-request-received";
    public static final String Q_FRIEND_REQUEST_ACCEPTED = "queue.notifications.friend-request-accepted";
    public static final String Q_LOBBY_INVITE = "queue.notifications.lobby-invite";
    public static final String Q_PLAYER_JOINED = "queue.notifications.player-joined-lobby";
    public static final String Q_ACHIEVEMENT_UNLOCKED = "queue.notifications.achievement-unlocked";
    public static final String Q_ORDER_COMPLETED = "queue.notifications.order-completed";
    public static final String Q_DIRECT_MESSAGE = "queue.notifications.direct-message";

    @Bean
    TopicExchange socialExchange() {
        return new TopicExchange(EXCHANGE_SOCIAL, true, false);
    }

    @Bean
    TopicExchange sessionExchange() {
        return new TopicExchange(EXCHANGE_SESSION, true, false);
    }

    @Bean
    TopicExchange achievementsExchange() {
        return new TopicExchange(EXCHANGE_ACHIEVEMENTS, true, false);
    }

    @Bean
    TopicExchange storeExchange() {
        return new TopicExchange(EXCHANGE_STORE, true, false);
    }

    @Bean
    TopicExchange chatExchange() {
        return new TopicExchange(EXCHANGE_CHAT, true, false);
    }

    @Bean
    Queue friendRequestReceivedQueue() {
        return QueueBuilder.durable(Q_FRIEND_REQUEST_RECEIVED).build();
    }

    @Bean
    Queue friendRequestAcceptedQueue() {
        return QueueBuilder.durable(Q_FRIEND_REQUEST_ACCEPTED).build();
    }

    @Bean
    Queue lobbyInviteQueue() {
        return QueueBuilder.durable(Q_LOBBY_INVITE).build();
    }

    @Bean
    Queue playerJoinedLobbyQueue() {
        return QueueBuilder.durable(Q_PLAYER_JOINED).build();
    }

    @Bean
    Queue achievementUnlockedQueue() {
        return QueueBuilder.durable(Q_ACHIEVEMENT_UNLOCKED).build();
    }

    @Bean
    Queue orderCompletedQueue() {
        return QueueBuilder.durable(Q_ORDER_COMPLETED).build();
    }

    @Bean
    Queue directMessageQueue() {
        return QueueBuilder.durable(Q_DIRECT_MESSAGE).build();
    }

    @Bean
    Binding bindFriendRequestReceived() {
        return BindingBuilder.bind(friendRequestReceivedQueue())
                .to(socialExchange()).with("social.friend-request.received");
    }

    @Bean
    Binding bindFriendRequestAccepted() {
        return BindingBuilder.bind(friendRequestAcceptedQueue())
                .to(socialExchange()).with("social.friend-request.accepted");
    }

    @Bean
    Binding bindLobbyInvite() {
        return BindingBuilder.bind(lobbyInviteQueue())
                .to(sessionExchange()).with("session.lobby.invite");
    }

    @Bean
    Binding bindPlayerJoined() {
        return BindingBuilder.bind(playerJoinedLobbyQueue())
                .to(sessionExchange()).with("session.lobby.joined");
    }

    @Bean
    Binding bindAchievementUnlocked() {
        return BindingBuilder.bind(achievementUnlockedQueue())
                .to(achievementsExchange()).with("achievement.unlocked");
    }

    @Bean
    Binding bindOrderCompleted() {
        return BindingBuilder.bind(orderCompletedQueue())
                .to(storeExchange()).with("store.order.completed");
    }

    @Bean
    Binding bindDirectMessage() {
        return BindingBuilder.bind(directMessageQueue())
                .to(chatExchange()).with("chat.direct-message");
    }
}
