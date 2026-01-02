package be.kdg.team22.socketservice.config;

public class TokenException extends RuntimeException {
    public TokenException() {
        super("Connection has no authentication token");
    }
}
