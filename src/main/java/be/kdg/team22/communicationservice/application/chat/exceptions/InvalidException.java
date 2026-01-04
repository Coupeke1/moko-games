package be.kdg.team22.communicationservice.application.chat.exceptions;

public class InvalidException extends RuntimeException {
    private InvalidException(String message) {
        super(message);
    }

    public static InvalidException content() {
        return new InvalidException("Content is not valid");
    }

    public static InvalidException game() {
        return new InvalidException("Game is not valid");
    }
}
