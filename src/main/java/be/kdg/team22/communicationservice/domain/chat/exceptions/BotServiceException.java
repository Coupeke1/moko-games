package be.kdg.team22.communicationservice.domain.chat.exceptions;

public class BotServiceException extends RuntimeException {
    private BotServiceException(String message) {
        super(message);
    }

    private BotServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public static BotServiceException unavailable() {
        return new BotServiceException("Bot Service is currently unavailable");
    }

    public static BotServiceException badResponse(String details) {
        return new BotServiceException(String.format("Bot Service returned an invalid response:\n%s", details));
    }

    public static BotServiceException requestFailed(Throwable cause) {
        return new BotServiceException("Failed to communicate with bot service", cause);
    }
}