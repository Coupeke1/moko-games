package be.kdg.team22.sessionservice.domain.lobby.exceptions;

public class ServiceNotReachableException extends RuntimeException {
    public ServiceNotReachableException(String service) {
        super(String.format("Service '%s' was not reachable", service));
    }

    public static ServiceNotReachableException socialServiceNotReachable() {
        return new ServiceNotReachableException("social-service");
    }

    public static ServiceNotReachableException userServiceNotReachable() {
        return new ServiceNotReachableException("user-service");
    }

}