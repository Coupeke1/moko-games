package be.kdg.team22.communicationservice.infrastructure.social;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Component
public class ExternalSocialRepository {
    private final RestClient client;

    public ExternalSocialRepository(@Qualifier("socialService") final RestClient client) {
        this.client = client;
    }

    public boolean areFriends(final UUID userId, final UUID friendId, final Jwt token) {
        try {
            List<FriendResponse> friends = client.get().uri("/api/friends").header("Authorization", "Bearer " + token.getTokenValue()).retrieve().body(new ParameterizedTypeReference<>() {});

            if (friends == null) return false;
            return friends.stream().anyMatch(f -> f.id().equals(friendId));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                return false;

            throw e;
        }
    }
}

