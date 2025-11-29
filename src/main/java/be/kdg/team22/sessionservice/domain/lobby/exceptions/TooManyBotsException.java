package be.kdg.team22.sessionservice.domain.lobby.exceptions;

public class TooManyBotsException extends RuntimeException {
    public TooManyBotsException() {
        super("Tic Tac Toe lobbies only support one bot player.");
    }
}