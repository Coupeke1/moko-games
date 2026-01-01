package be.kdg.team22.tictactoeservice.domain.document.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException notFoundException() {
        return new ResourceNotFoundException("could not find resource");
    }
}
