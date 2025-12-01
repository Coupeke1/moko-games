package be.kdg.team22.sessionservice.domain.lobby.exceptions;

public class PlayersException extends RuntimeException {
    private PlayersException(String message) {
        super(message);
    }

    public static PlayersException tooMany() {
        return new PlayersException("There are too many players in this lobby");
    }

    public static PlayersException tooLittle() {
        return new PlayersException("There are not enough players in this lobby");
    }
}
