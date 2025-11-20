package be.kdg.team22.socialservice.friends.domain;

public record Username(String username) {
    public Username(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        this.username = username.trim();
    }

    public NotFoundException notFound() {
        return new NotFoundException("Username [" + username + "] not found");
    }

    @Override
    public String toString() {
        return username;
    }
}
