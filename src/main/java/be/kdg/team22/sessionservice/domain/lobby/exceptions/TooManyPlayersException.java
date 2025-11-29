package be.kdg.team22.sessionservice.domain.lobby.exceptions;

public class TooManyPlayersException extends RuntimeException {
    public TooManyPlayersException() {
        super("Tic-tac-toe lobbies only support one player when playing with bots");
    }
}