package be.kdg.team22.sessionservice.infrastructure.games;

import be.kdg.team22.sessionservice.domain.lobby.settings.GameSettings;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.UUID;

public interface GameClient {
    StartGameResponse startGame(StartGameRequest request, Jwt token);

    record StartGameRequest(
            UUID lobbyId,
            UUID gameId,
            List<UUID> players,
            GameSettings settings
    ) {
    }


    record StartGameResponse(UUID gameInstanceId) {
    }
}
