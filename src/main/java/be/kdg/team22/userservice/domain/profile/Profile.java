package be.kdg.team22.userservice.domain.profile;

import org.jmolecules.ddd.annotation.AggregateRoot;

import java.time.Instant;

@AggregateRoot
public class Profile {
    private final ProfileId id;
    private String username;
    private String email;
    private final Instant createdAt;

    public Profile(ProfileId id, String username, String email, Instant createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
    }

    public Profile(ProfileId id, String username, String email) {
        this(id, username, email, Instant.now());
    }

    public void update(final String username, final String email) {
        this.username = username;
        this.email = email;
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