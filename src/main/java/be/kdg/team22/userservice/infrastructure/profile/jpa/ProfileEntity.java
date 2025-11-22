package be.kdg.team22.userservice.infrastructure.profile.jpa;

import be.kdg.team22.userservice.domain.profile.Profile;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "profiles")
public class ProfileEntity {
    @Id
    @Column(name = "keycloak_id", nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ProfileEntity() {
    }

    public ProfileEntity(final UUID id, final String username, final Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public static ProfileEntity from(final Profile profile) {
        return new ProfileEntity(profile.id().value(), profile.username(), profile.createdAt());
    }

    public Profile to() {
        return new Profile(new ProfileId(id), username, createdAt);
    }
}