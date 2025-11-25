package be.kdg.team22.gamesservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestcontainersConfig {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.7-alpine"));
    }

    @Bean
    @ServiceConnection
    RabbitMQContainer rabbitContainer() {
        return new RabbitMQContainer(DockerImageName.parse("rabbitmq:latest"));
    }
}
