package be.kdg.team22.checkersservice.config;

import be.kdg.team22.checkersservice.application.GameRegistrationStartup;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestcontainersConfig {

    @Bean
    GenericContainer<?> redisContainer() {
        GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:8.4.0-alpine"))
                .withExposedPorts(6379);
        redis.start();
        System.setProperty("spring.data.redis.host", redis.getHost());
        System.setProperty("spring.data.redis.port", redis.getMappedPort(6379).toString());
        return redis;
    }

    @Bean
    @Primary
    GameRegistrationStartup gameRegistrationStartup() {
        return mock(GameRegistrationStartup.class);
    }
}