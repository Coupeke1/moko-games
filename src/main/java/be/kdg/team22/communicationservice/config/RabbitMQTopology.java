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
    public static final String EXCHANGE_USER_SOCKET = "user.direct.exchange";
    public static final String EXCHANGE_SUBSCRIBED_SOCKET = "socket.subscription.exchange";


    public static final String QUEUE_FRIEND_REQUEST_RECEIVED = "queue.notifications.friend-request-received";
    public static final String QUEUE_FRIEND_REQUEST_ACCEPTED = "queue.notifications.friend-request-accepted";
    public static final String QUEUE_LOBBY_INVITE = "queue.notifications.lobby-invite";
    public static final String QUEUE_PLAYER_JOINED = "queue.notifications.player-joined-lobby";
    public static final String QUEUE_ACHIEVEMENT_UNLOCKED = "queue.notifications.achievement-unlocked";
    public static final String QUEUE_ORDER_COMPLETED = "queue.notifications.order-completed";
    public static final String QUEUE_DIRECT_MESSAGE = "queue.notifications.direct-message";
    public static final String QUEUE_USER_SOCKET = "user.socket.queue";
    public static final String QUEUE_SUBSCRIBED_SOCKET = "user.subscribe.queue";

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
    TopicExchange userSocketExchange() {
        return new TopicExchange(EXCHANGE_USER_SOCKET, true, false);
    }

    @Bean
    TopicExchange subscribedSocketExchange() {
        return new TopicExchange(EXCHANGE_SUBSCRIBED_SOCKET, true, false);
    }

    @Bean
    Queue friendRequestReceivedQueue() {
        return QueueBuilder.durable(QUEUE_FRIEND_REQUEST_RECEIVED).build();
    }

    @Bean
    Queue friendRequestAcceptedQueue() {
        return QueueBuilder.durable(QUEUE_FRIEND_REQUEST_ACCEPTED).build();
    }

    @Bean
    Queue lobbyInviteQueue() {
        return QueueBuilder.durable(QUEUE_LOBBY_INVITE).build();
    }

    @Bean
    Queue playerJoinedLobbyQueue() {
        return QueueBuilder.durable(QUEUE_PLAYER_JOINED).build();
    }

    @Bean
    Queue achievementUnlockedQueue() {
        return QueueBuilder.durable(QUEUE_ACHIEVEMENT_UNLOCKED).build();
    }

    @Bean
    Queue orderCompletedQueue() {
        return QueueBuilder.durable(QUEUE_ORDER_COMPLETED).build();
    }

    @Bean
    Queue directMessageQueue() {
        return QueueBuilder.durable(QUEUE_DIRECT_MESSAGE).build();
    }

    @Bean
    Queue userSocketQueue() {
        return QueueBuilder.durable(QUEUE_USER_SOCKET).build();
    }

    @Bean
    Queue subscribedSocketQueue() {
        return QueueBuilder.durable(QUEUE_SUBSCRIBED_SOCKET).build();
    }

    @Bean
    Binding bindFriendRequestReceived() {
        return BindingBuilder.bind(friendRequestReceivedQueue()).to(socialExchange()).with("social.friend-request.received");
    }

    @Bean
    Binding bindFriendRequestAccepted() {
        return BindingBuilder.bind(friendRequestAcceptedQueue()).to(socialExchange()).with("social.friend-request.accepted");
    }

    @Bean
    Binding bindLobbyInvite() {
        return BindingBuilder.bind(lobbyInviteQueue()).to(sessionExchange()).with("session.lobby.invite");
    }

    @Bean
    Binding bindPlayerJoined() {
        return BindingBuilder.bind(playerJoinedLobbyQueue()).to(sessionExchange()).with("session.lobby.joined");
    }

    @Bean
    Binding bindAchievementUnlocked() {
        return BindingBuilder.bind(achievementUnlockedQueue()).to(achievementsExchange()).with("achievement.unlocked");
    }

    @Bean
    Binding bindOrderCompleted() {
        return BindingBuilder.bind(orderCompletedQueue()).to(storeExchange()).with("store.order.completed");
    }

    @Bean
    Binding bindDirectMessage() {
        return BindingBuilder.bind(directMessageQueue()).to(chatExchange()).with("chat.direct-message");
    }

    @Bean
    Binding bindUserSocketMessage() {
        return BindingBuilder.bind(userSocketQueue()).to(userSocketExchange()).with("user.message");
    }

    @Bean
    Binding bindSubscribedSocketMessage() {
        return BindingBuilder.bind(subscribedSocketQueue()).to(subscribedSocketExchange()).with("socket.subscribe.user.queue.chat");
    }
}
