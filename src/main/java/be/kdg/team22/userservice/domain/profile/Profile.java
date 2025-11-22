package be.kdg.team22.userservice.domain.profile;

import org.jmolecules.ddd.annotation.AggregateRoot;

import java.time.Instant;

@AggregateRoot
public class Profile {
    private final ProfileId id;
    private final String username;
    private final Instant createdAt;

    public Profile(final ProfileId id, final String username, final Instant createdAt) {
        this.id = id;
        this.username = username;
        this.createdAt = createdAt;
    }

    public Profile(final ProfileId id, final String username) {
        this(id, username, Instant.now());
    }

    public ProfileId id() {
        return id;
    }

    public String username() {
        return username;
    }

    public Instant createdAt() {
        return createdAt;
    }
}