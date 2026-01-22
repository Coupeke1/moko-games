package be.kdg.team22.sessionservice.infrastructure.chat;

import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.CommunicationServiceException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.NotReachableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ExternalChatRepository {
    private final RestClient client;

    public ExternalChatRepository(@Qualifier("communicationService") final RestClient client) {
        this.client = client;
    }

    public void createLobbyChat(final LobbyId id, final Jwt token) {
        try {
            client.post().uri("/api/chat/channel/lobby/{id}", id.value().toString()).header("Authorization", String.format("Bearer %s", token.getTokenValue())).retrieve().toBodilessEntity();
        } catch (HttpClientErrorException e) {
            throw new CommunicationServiceException("CommunicationService refused chat creation: " + e.getStatusCode());
        } catch (RestClientException e) {
            throw NotReachableException.CommunicationService();
        }
    }
}