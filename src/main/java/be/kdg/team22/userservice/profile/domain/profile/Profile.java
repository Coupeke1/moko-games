package be.kdg.team22.userservice.profile.domain.profile;

import java.time.Instant;
import java.util.Objects;

public class Profile {

    private final ProfileId id;
    private final Instant createdAt;
    private String username;
    private String email;

    public Profile(ProfileId id, String username, String email, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.username = Objects.requireNonNull(username);
        this.email = Objects.requireNonNull(email);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static Profile createNew(ProfileId id, String username, String email) {
        return new Profile(id, username, email, Instant.now());
    }

    public void update(String username, String email) {
        this.username = Objects.requireNonNull(username);
        this.email = Objects.requireNonNull(email);
    }

    public ProfileId id() {
        return id;
    }

    public String username() {
        return username;
    }

    public String email() {
        return email;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
