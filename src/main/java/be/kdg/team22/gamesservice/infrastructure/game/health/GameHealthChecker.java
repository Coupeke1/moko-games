package be.kdg.team22.gamesservice.infrastructure.game.health;

import be.kdg.team22.gamesservice.api.game.models.RegisterGameRequest;
import be.kdg.team22.gamesservice.domain.game.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GameHealthChecker {
    private final Logger logger = LoggerFactory.getLogger(GameHealthChecker.class);

    public boolean isHealthy(Game game) {
        return checkEndpoint(game.baseUrl(), game.healthEndpoint());
    }

    public boolean isHealthy(RegisterGameRequest request) {
        return checkEndpoint(request.backendUrl(), request.healthEndpoint());
    }

    private boolean checkEndpoint(String baseUrl, String healthEndpoint) {
        try {
            logger.info("Checking game health endpoint : {}/{}", baseUrl, healthEndpoint);
            RestClient client = RestClient.builder().baseUrl(baseUrl).build();
            ResponseEntity<Void> response = client.get()
                    .uri(healthEndpoint)
                    .retrieve()
                    .toBodilessEntity();
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return false;
        }
    }
}
