package be.kdg.team22.userservice.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class GameServiceHealthIndicator implements HealthIndicator {
    @Value("${business.game-service.url}")
    private String gameServiceUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Health health() {
        String url = this.gameServiceUrl + "/actuator/health";

        try {
            var response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return Health.up().withDetail("game-service", "reachable").build();
            } else {
                return Health.down()
                        .withDetail("game-service", "unexpected status: " + response.getStatusCode())
                        .build();
            }
        } catch (RestClientException e) {
            return Health.down(e)
                    .withDetail("game-service", "unreachable")
                    .build();
        }
    }
}
