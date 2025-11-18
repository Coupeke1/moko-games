package be.kdg.team22.userservice.profile.infrastructure.db.repositories.profile.jpa;

import be.kdg.team22.userservice.profile.domain.profile.Profile;
import be.kdg.team22.userservice.profile.domain.profile.ProfileId;
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

    private String username;

    private String email;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ProfileEntity() {
    }

    public ProfileEntity(UUID id, String username, String email, Instant createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
    }

    public static ProfileEntity fromDomain(Profile profile) {
        return new ProfileEntity(
                profile.id().value(),
                profile.username(),
                profile.email(),
                profile.createdAt()
        );
    }

    public Profile toDomain() {
        return new Profile(
                new ProfileId(id),
                username,
                email,
                createdAt
        );
    }
}
