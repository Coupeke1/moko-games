package be.kdg.team22.tictactoeservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "game-registry")
public class GameRegistryProperties {
    private boolean enabled;
    private String registryUrl;
    private GameProperties game;

    public static class GameProperties {
        private String name;
        private String title;
        private String description;
        private String image;
        private String backendUrl;
        private String frontendUrl;
        private String startEndpoint;
        private String healthEndpoint;
    }
}