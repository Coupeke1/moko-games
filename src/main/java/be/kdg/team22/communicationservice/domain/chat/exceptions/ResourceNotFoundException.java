package be.kdg.team22.communicationservice.domain.chat.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException notFoundException() {
        return new ResourceNotFoundException("could not find resource");
    }
}
