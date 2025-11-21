package be.kdg.team22.sessionservice.infrastructure.lobby.db.friends;

import be.kdg.team22.sessionservice.domain.lobby.exceptions.ServiceNotReachableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.UUID;

@Component
public class ExternalFriendsRepository {

    private final RestClient client;

    public ExternalFriendsRepository(@Qualifier("socialService") RestClient client) {
        this.client = client;
    }

    public List<UUID> getFriendIds(UUID ownerId) {
        try {
            FriendsOverviewResponse response = client.get()
                    .uri("/api/friends")
                    .retrieve()
                    .body(FriendsOverviewResponse.class);

            if (response == null) return List.of();

            return response.friends()
                    .stream()
                    .map(FriendsResponse::userId)
                    .toList();

        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) return List.of();
            throw ex;
        } catch (RestClientException ex) {
            throw ServiceNotReachableException.socialServiceNotReachable();
        }
    }
}
