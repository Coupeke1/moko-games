package be.kdg.team22.userservice.domain.profile;

import org.jmolecules.ddd.annotation.AggregateRoot;

import java.time.Instant;

@AggregateRoot
public class Profile {
    private final ProfileId id;
    private final ProfileName username;
    private final ProfileEmail email;
    private final String description;
    private final Statistics statistics;
    private final Modules modules;
    private final Instant createdAt;

    public Profile(final ProfileId id, final ProfileName username, final ProfileEmail email, final String description, final Statistics statistics, final Modules modules, final Instant createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.description = description;
        this.statistics = statistics;
        this.modules = modules;
        this.createdAt = createdAt;
    }

    public Profile(final ProfileId id, final ProfileName username, final ProfileEmail email, final String description) {
        this(id, username, email, description, new Statistics(0, 0), new Modules(false, false), Instant.now());
    }

    public ProfileId id() {
        return id;
    }

    public ProfileName username() {
        return username;
    }

    public ProfileEmail email() {
        return email;
    }

    public String description() {
        return description;
    }

    public Statistics statistics() {
        return statistics;
    }

    public Modules modules() {
        return modules;
    }

    public Instant createdAt() {
        return createdAt;
    }
}