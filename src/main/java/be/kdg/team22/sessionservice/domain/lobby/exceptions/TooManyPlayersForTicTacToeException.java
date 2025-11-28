package be.kdg.team22.sessionservice.domain.lobby.exceptions;

public class TooManyPlayersForTicTacToeException extends RuntimeException {
    public TooManyPlayersForTicTacToeException() {
        super("Tic-tac-toe lobbies only support one human owner and one bot player.");
    }
}