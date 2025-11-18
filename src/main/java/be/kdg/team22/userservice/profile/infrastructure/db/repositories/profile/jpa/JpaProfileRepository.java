package be.kdg.team22.userservice.profile.infrastructure.db.repositories.profile.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaProfileRepository extends JpaRepository<ProfileEntity, UUID> {
}
