package be.kdg.team22.sessionservice.domain.lobby.exceptions.domain;

public class MaxPlayersTooSmallException extends RuntimeException {
    public MaxPlayersTooSmallException(int currentPlayers, int maxPlayers) {
        super("maxPlayers (" + maxPlayers + ") cannot be smaller than current player count (" + currentPlayers + ")");
    }
}
