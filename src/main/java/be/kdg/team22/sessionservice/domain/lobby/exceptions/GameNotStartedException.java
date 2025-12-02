package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import be.kdg.team22.sessionservice.domain.lobby.LobbyId;

public class GameNotStartedException extends RuntimeException {
    public GameNotStartedException(LobbyId id) {
        super("Game for lobby with id '%s' did not start".formatted(id));
    }
}
