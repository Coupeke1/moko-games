package be.kdg.team22.sessionservice.domain.lobby.exceptions;

public class NotReachableException extends RuntimeException {
    public NotReachableException(String message) {
        super(message);
    }

    public static NotReachableException CommunicationService() {
        return new NotReachableException("Service 'Communication-service' is unreachable");
    }
}
