package be.kdg.team22.communicationservice.infrastructure.lobby;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class ExternalSessionRepository {
    private final RestClient client;

    public ExternalSessionRepository(@Qualifier("sessionService") final RestClient client) {
        this.client = client;
    }

    public boolean isPlayerInLobby(final UUID userId, final UUID lobbyId, final Jwt token) {
        try {
            LobbyResponse response = client.get().uri("/api/lobbies/{id}", lobbyId).header("Authorization", "Bearer " + token.getTokenValue()).retrieve().body(LobbyResponse.class);

            if (response == null || response.players() == null)
                return false;

            return response.players().stream().anyMatch(p -> p.id().equals(userId));

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                return false;
            throw e;
        }
    }
}