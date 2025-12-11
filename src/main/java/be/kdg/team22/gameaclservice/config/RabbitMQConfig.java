package be.kdg.team22.gameaclservice.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RabbitMQConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "rabbitmq.platform")
    public RabbitMQProperties platformRabbitProperties() {
        return new RabbitMQProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "rabbitmq.chess")
    public RabbitMQProperties chessRabbitProperties() {
        return new RabbitMQProperties();
    }

    @Bean(name = "platformConnectionFactory")
    @Primary
    public ConnectionFactory platformConnectionFactory(@Qualifier("platformRabbitProperties") RabbitMQProperties platformRabbitProperties) {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(platformRabbitProperties.host);
        factory.setPort(platformRabbitProperties.port);
        factory.setUsername(platformRabbitProperties.username);
        factory.setPassword(platformRabbitProperties.password);
        return factory;
    }

    @Bean(name = "chessConnectionFactory")
    public ConnectionFactory chessConnectionFactory(@Qualifier("chessRabbitProperties") RabbitMQProperties chessRabbitProperties) {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(chessRabbitProperties.host);
        factory.setPort(chessRabbitProperties.port);
        factory.setUsername(chessRabbitProperties.username);
        factory.setPassword(chessRabbitProperties.password);
        return factory;
    }

    @Bean(name = "platformListenerFactory")
    public SimpleRabbitListenerContainerFactory platformListenerFactory(@Qualifier("platformConnectionFactory") ConnectionFactory platformConnectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(platformConnectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }

    @Bean
    Jackson2JsonMessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    public static class RabbitMQProperties {
        private String host;
        private int port;
        private String username;
        private String password;

        public String host() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int port() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String username() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String password() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
