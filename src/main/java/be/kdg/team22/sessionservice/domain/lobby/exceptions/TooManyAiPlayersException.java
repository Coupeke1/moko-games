package be.kdg.team22.sessionservice.domain.lobby.exceptions;

public class TooManyAiPlayersException extends RuntimeException {
    public TooManyAiPlayersException() {
        super("Tic-tac-toe lobbies only support one bot player.");
    }
}