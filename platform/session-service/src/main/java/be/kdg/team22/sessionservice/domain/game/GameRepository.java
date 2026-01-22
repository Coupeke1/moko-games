package be.kdg.team22.sessionservice.domain.game;

import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.infrastructure.games.StartGameRequest;
import be.kdg.team22.sessionservice.infrastructure.games.StartGameResponse;
import org.springframework.security.oauth2.jwt.Jwt;

public interface GameRepository {
    String findGameNameById(GameId gameId);

    StartGameResponse startGame(StartGameRequest request, Jwt token);
}