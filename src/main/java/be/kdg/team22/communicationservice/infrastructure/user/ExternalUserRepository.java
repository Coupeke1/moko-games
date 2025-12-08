package be.kdg.team22.communicationservice.infrastructure.user;

import be.kdg.team22.communicationservice.domain.notification.exceptions.ServiceUnavailableException;
import be.kdg.team22.communicationservice.domain.notification.exceptions.UserPreferencesNotFoundException;
import be.kdg.team22.communicationservice.domain.notification.exceptions.UserProfileNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

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
            ProfileResponse response = client.get()
                    .uri("/api/profiles/{id}", userId)
                    .retrieve()
                    .body(ProfileResponse.class);

            if (response == null) {
                throw new UserProfileNotFoundException(userId);
            }

            return response;

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new UserProfileNotFoundException(userId);
            }
            throw e;
        } catch (RestClientException e) {
            throw ServiceUnavailableException.userServiceUnavailable();
        }
    }

    public PreferencesResponse getPreferencesByUserId(final UUID userId) {
        try {
            PreferencesResponse response = client.get()
                    .uri("/api/profiles/{id}/preferences", userId)
                    .retrieve()
                    .body(PreferencesResponse.class);

            if (response == null) {
                throw new UserPreferencesNotFoundException(userId);
            }

            return response;

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new UserPreferencesNotFoundException(userId);
            }
            throw e;
        } catch (RestClientException e) {
            throw ServiceUnavailableException.userServiceUnavailable();
        }
    }
}