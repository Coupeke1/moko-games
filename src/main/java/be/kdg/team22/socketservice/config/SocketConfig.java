package be.kdg.team22.socketservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class SocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtDecoder decoder;

    @Value("${spring.stomp.relay.host}")
    private String host;

    @Value("${spring.stomp.relay.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    public SocketConfig(final JwtDecoder decoder) {this.decoder = decoder;}

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();}

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableStompBrokerRelay("/queue", "/topic").setRelayHost(host).setRelayPort(port).setClientLogin(username).setClientPasscode(password).setSystemLogin(username).setSystemPasscode(password).setUserDestinationBroadcast("/topic/unresolved-user").setUserRegistryBroadcast("/topic/user-registry");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor == null || accessor.getCommand() != StompCommand.CONNECT)
                    return message;

                String header = accessor.getFirstNativeHeader("Authorization");
                if (header == null || header.startsWith("Bearer "))
                    return message;

                Jwt token = decoder.decode(header.substring(7));
                JwtAuthenticationToken authentication = new JwtAuthenticationToken(token);
                accessor.setUser(authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("STOMP CONNECT USER = " + authentication.getName());

                if (accessor.getUser() != null)
                    System.out.println("WEBSOCKET USER = " + accessor.getUser().getName());

                System.out.println("WEBSOCKET USER = <null>");
                return message;
            }
        });
    }
}