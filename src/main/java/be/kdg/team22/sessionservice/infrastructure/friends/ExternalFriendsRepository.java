package be.kdg.team22.sessionservice.infrastructure.friends;

import be.kdg.team22.sessionservice.domain.friends.exceptions.NotReachableException;
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

    public List<UUID> getFriendIds(final String token) {
        try {
            FriendsOverviewResponse response = client.get().uri("/api/friends").header("Authorization", "Bearer " + token).retrieve().body(FriendsOverviewResponse.class);

            if (response == null)
                return List.of();

            return response.friends().stream().map(FriendsResponse::userId).toList();
        } catch (
                HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND)
                return List.of();
            throw exception;
        } catch (
                RestClientException exception) {
            throw new NotReachableException();
        }
    }
}