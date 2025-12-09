package be.kdg.team22.communicationservice.domain.chat.exceptions;

public class BotServiceException extends RuntimeException {

    public BotServiceException(String message) {
        super(message);
    }

    public BotServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public static BotServiceException unavailable() {
        return new BotServiceException("Bot service is currently unavailable");
    }

    public static BotServiceException badResponse(String details) {
        return new BotServiceException("Bot service returned an invalid response: " + details);
    }

    public static BotServiceException requestFailed(Throwable cause) {
        return new BotServiceException("Failed to communicate with bot service", cause);
    }
}