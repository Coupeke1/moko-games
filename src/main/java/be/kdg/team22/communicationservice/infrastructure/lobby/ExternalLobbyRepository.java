package be.kdg.team22.communicationservice.infrastructure.lobby;

import be.kdg.team22.communicationservice.domain.chat.channel.LobbyId;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import be.kdg.team22.communicationservice.infrastructure.lobby.models.LobbyNotFoundException;
import be.kdg.team22.communicationservice.infrastructure.lobby.models.LobbyResponse;
import be.kdg.team22.communicationservice.infrastructure.lobby.models.PlayerModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class ExternalLobbyRepository {
    private final RestClient client;

    public ExternalLobbyRepository(@Qualifier("sessionService") final RestClient client) {
        this.client = client;
    }

    public boolean isPlayerInLobby(final UUID userId, final UUID lobbyId, final Jwt token) {
        try {
            LobbyResponse response = client.get().uri("/api/lobbies/{id}", lobbyId).header("Authorization", "Bearer " + token.getTokenValue()).retrieve().body(LobbyResponse.class);

            if (response == null || response.players() == null)
                return false;

            return response.players().stream().anyMatch(p -> p.id().equals(userId));

        } catch (
                HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND)
                return false;

            throw exception;
        }
    }

    public List<PlayerModel> findPlayers(final UUID id, final Jwt token) {
        try {
            LobbyResponse response = client.get().uri("/api/lobbies/{id}", id).header("Authorization", "Bearer " + token.getTokenValue()).retrieve().body(LobbyResponse.class);

            if (response == null || response.players() == null)
                return Collections.emptyList();

            return response.players().stream().toList();

        } catch (
                HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND)
                throw new LobbyNotFoundException(LobbyId.from(id));

            throw exception;
        }
    }

    public List<PlayerId> findPlayers(final UUID id) {
        try {
            return client.get().uri("/api/lobbies/{id}/players", id).retrieve().body(new ParameterizedTypeReference<List<PlayerId>>() {}).stream().map(playerId -> new PlayerId(playerId.value())).toList();
        } catch (
                HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND)
                return Collections.emptyList();

            throw exception;
        }
    }
}