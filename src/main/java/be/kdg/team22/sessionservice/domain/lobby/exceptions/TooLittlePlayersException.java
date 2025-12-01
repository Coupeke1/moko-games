package be.kdg.team22.sessionservice.domain.lobby.exceptions;

public class TooLittlePlayersException extends RuntimeException {
    public TooLittlePlayersException() {
        super("There are too little players in this lobby");
    }
}