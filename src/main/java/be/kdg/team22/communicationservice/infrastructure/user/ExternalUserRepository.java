package be.kdg.team22.communicationservice.infrastructure.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class ExternalUserRepository {
    private final RestClient client;

    public ExternalUserRepository(@Qualifier("userService") final RestClient client) {
        this.client = client;
    }

    public PreferencesResponse getPreferences(final String jwtToken) {
        try {
            return client.get()
                    .uri("/profiles/me/preferences")
                    .header("Authorization", "Bearer " + jwtToken)
                    .retrieve()
                    .body(PreferencesResponse.class);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return PreferencesResponse.defaultPreferences();
            }
            throw e;
        }
    }

    public ProfileResponse getProfile(final UUID userId) {
        try {
            return client.get()
                    .uri("/api/profiles/{id}", userId)
                    .retrieve()
                    .body(ProfileResponse.class);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            throw e;
        }
    }

    public PreferencesResponse getPreferencesByUserId(final UUID userId) {
        // Note: The user-service currently doesn't have a /api/profiles/{id}/preferences endpoint
        // This would need to be added to the user-service to properly fetch preferences by userId
        // For now, we return default preferences which allows all notifications
        // TODO: Add endpoint in user-service: GET /api/profiles/{id}/preferences
        return PreferencesResponse.defaultPreferences();
    }
}