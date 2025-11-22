package be.kdg.team22.userservice.infrastructure.profile;

import be.kdg.team22.userservice.domain.profile.Profile;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.domain.profile.ProfileName;
import be.kdg.team22.userservice.domain.profile.ProfileRepository;
import be.kdg.team22.userservice.infrastructure.profile.jpa.JpaProfileRepository;
import be.kdg.team22.userservice.infrastructure.profile.jpa.ProfileEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DbProfileRepository implements ProfileRepository {
    private final JpaProfileRepository repository;

    public DbProfileRepository(final JpaProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Profile> findById(final ProfileId id) {
        return repository.findById(id.value()).map(ProfileEntity::to);
    }

    @Override
    public Optional<Profile> findByUsername(final ProfileName username) {
        return repository.findByUsername(username.value()).map(ProfileEntity::to);
    }

    @Override
    public void save(final Profile profile) {
        ProfileEntity entity = ProfileEntity.from(profile);
        repository.save(entity);
    }

    @Override
    public List<Profile> findAll() {
        return repository.findAll().stream().map(ProfileEntity::to).toList();
    }
}