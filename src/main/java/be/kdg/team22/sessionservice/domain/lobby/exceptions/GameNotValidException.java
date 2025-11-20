package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import java.util.UUID;

public class GameNotValidException extends RuntimeException {
    public GameNotValidException(UUID gameId) {
        super(String.format("Game with id '%s' is not valid", gameId));
    }
}