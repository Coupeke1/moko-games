package be.kdg.team22.socialservice.domain.friends.user.exceptions;

public class NotReachableException extends RuntimeException {
    public NotReachableException(String service) {
        super(String.format("Service '%s' is unreachable", service));
    }
}
