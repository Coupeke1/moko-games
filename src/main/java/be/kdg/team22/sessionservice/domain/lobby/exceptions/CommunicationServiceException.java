package be.kdg.team22.sessionservice.domain.lobby.exceptions;

public class CommunicationServiceException extends RuntimeException {
    public CommunicationServiceException(String message) {
        super(message);
    }
}