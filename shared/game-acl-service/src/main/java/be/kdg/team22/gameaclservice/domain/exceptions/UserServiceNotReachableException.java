package be.kdg.team22.gameaclservice.domain.exceptions;

public class UserServiceNotReachableException extends RuntimeException {
    public UserServiceNotReachableException(String url) {
        super(String.format("user-service is not reachable at '%s'", url));
    }
}
