package be.kdg.team22.storeservice.domain.exceptions;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }

    public static ServiceUnavailableException UserServiceUnavailable() {
        return new ServiceUnavailableException("User-Service is currently unavailable");
    }

    public static ServiceUnavailableException GameServiceUnavailable() {
        return new ServiceUnavailableException("Game-Service is currently unavailable");
    }
}
