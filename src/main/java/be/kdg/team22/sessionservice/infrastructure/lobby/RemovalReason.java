package be.kdg.team22.sessionservice.infrastructure.lobby;

public enum RemovalReason {
    KICKED("kicked");

    private final String value;

    RemovalReason(final String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}