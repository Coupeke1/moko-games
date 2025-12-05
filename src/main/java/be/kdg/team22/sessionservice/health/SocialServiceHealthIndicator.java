package be.kdg.team22.sessionservice.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class SocialServiceHealthIndicator implements HealthIndicator {
    @Value("${business.social-service.url}")
    private String socialServiceUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Health health() {
        String url = this.socialServiceUrl + "/actuator/health";

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return Health.up().withDetail("social-service", "reachable").build();
            } else {
                return Health.status("WARNING")
                        .withDetail("social-service", "unexpected status: " + response.getStatusCode())
                        .build();
            }
        } catch (RestClientException e) {
            return Health.status("WARNING")
                    .withDetail("social-service", "unreachable")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
