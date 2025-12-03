package be.kdg.team22.socialservice.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class UserServiceHealthIndicator implements HealthIndicator {
    @Value("${business.user-service.url}")
    private String userServiceUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Health health() {
        String url = this.userServiceUrl + "/actuator/health";

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return Health.up().withDetail("user-service", "reachable").build();
            } else {
                return Health.status("WARNING")
                        .withDetail("user-service", "unexpected status: " + response.getStatusCode())
                        .build();
            }
        } catch (RestClientException e) {
            return Health.status("WARNING")
                    .withDetail("user-service", "unreachable")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
