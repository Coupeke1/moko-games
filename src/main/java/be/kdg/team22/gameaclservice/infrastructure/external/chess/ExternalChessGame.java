package be.kdg.team22.gameaclservice.infrastructure.external.chess;

import be.kdg.team22.gameaclservice.domain.exceptions.chess.ChessGameNotReachableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Component
public class ExternalChessGame {
    private final RestClient client;

    public ExternalChessGame(@Qualifier("chessGame") final RestClient chessGameRestClient) {
        this.client = chessGameRestClient;
    }

    public ChessGameResponse createGame(UUID id, CreateChessGameRequest request) {
        try {
            return client.post()
                    .uri("/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(ChessGameResponse.class);
        } catch (RestClientException ex) {
            throw new ChessGameNotReachableException(client.toString());
        }
    }
}