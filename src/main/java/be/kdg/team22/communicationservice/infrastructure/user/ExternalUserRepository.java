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

    public NotificationsResponse getNotifications(final String token) {
        try {
            return client.get().uri("/profiles/me/preferences/notifications").header("Authorization", "Bearer " + token).retrieve().body(NotificationsResponse.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return NotificationsResponse.defaultPreferences();
            }
            throw e;
        }
    }

    public ProfileResponse getProfile(final UUID id) {
        try {
            ProfileResponse response = client.get().uri("/api/profiles/{id}", id).retrieve().body(ProfileResponse.class);

            if (response == null) {
                throw new UserProfileNotFoundException(id);
            }

            return response;

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new UserProfileNotFoundException(id);
            }
            throw e;
        } catch (RestClientException e) {
            throw ServiceUnavailableException.userServiceUnavailable();
        }
    }

    public NotificationsResponse getNotificationsByUser(final UUID id) {
        try {
            NotificationsResponse response = client.get().uri("/api/profiles/{id}/preferences/notifications", id).retrieve().body(NotificationsResponse.class);

            if (response == null)
                throw new UserPreferencesNotFoundException(id);

            return response;

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new UserPreferencesNotFoundException(id);
            }
            throw e;
        } catch (RestClientException e) {
            throw ServiceUnavailableException.userServiceUnavailable();
        }
    }
}