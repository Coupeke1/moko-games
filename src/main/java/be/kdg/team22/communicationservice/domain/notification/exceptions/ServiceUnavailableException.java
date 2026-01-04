package be.kdg.team22.communicationservice.domain.notification.exceptions;

public class ServiceUnavailableException extends RuntimeException {

    private ServiceUnavailableException(String message) {
        super(message);
    }

    public static ServiceUnavailableException userServiceUnavailable() {
        return new ServiceUnavailableException("User service is currently unavailable. Please try again later.");
    }

    public static ServiceUnavailableException gamesServiceUnavailable() {
        return new ServiceUnavailableException("Games service is currently unavailable. Please try again later.");
    }
}

