package be.kdg.team22.userservice.infrastructure.db.repositories;

import be.kdg.team22.userservice.domain.profile.Profile;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.domain.profile.ProfileRepository;
import be.kdg.team22.userservice.infrastructure.db.repositories.jpa.JpaProfileRepository;
import be.kdg.team22.userservice.infrastructure.db.repositories.jpa.ProfileEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DbProfileRepository implements ProfileRepository {
    private final JpaProfileRepository repository;

    public DbProfileRepository(JpaProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        return repository.findById(id.value()).map(ProfileEntity::toDomain);
    }

    @Override
    public Optional<Profile> findByUsername(String username) {
        return repository.findByUsername(username).map(ProfileEntity::toDomain);
    }

    @Override
    public void save(Profile profile) {
        ProfileEntity entity = ProfileEntity.fromDomain(profile);
        repository.save(entity);
    }

    @Override
    public List<Profile> findAll() {
        return repository.findAll().stream().map(ProfileEntity::toDomain).toList();
    }
}