package be.kdg.team22.communicationservice.infrastructure.lobby;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class ExternalSessionRepository {

    private final RestClient client;

    public ExternalSessionRepository(@Qualifier("sessionService") RestClient client) {
        this.client = client;
    }

    public boolean isPlayerInLobby(String userId, String lobbyId, String jwtToken) {
        try {
            LobbyResponse response = client.get()
                    .uri("/api/lobbies/{id}", lobbyId)
                    .header("Authorization", "Bearer " + jwtToken)
                    .retrieve()
                    .body(LobbyResponse.class);

            if (response == null || response.players() == null)
                return false;

            return response.players().stream()
                    .anyMatch(p -> p.id().toString().equals(userId));

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                return false;
            throw e;
        }
    }
}