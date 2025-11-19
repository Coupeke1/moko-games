package be.kdg.team22.userservice.profile.infrastructure.db.repositories.profile;

import be.kdg.team22.userservice.profile.domain.profile.Profile;
import be.kdg.team22.userservice.profile.domain.profile.ProfileId;
import be.kdg.team22.userservice.profile.domain.profile.ProfileRepository;
import be.kdg.team22.userservice.profile.infrastructure.db.repositories.profile.jpa.JpaProfileRepository;
import be.kdg.team22.userservice.profile.infrastructure.db.repositories.profile.jpa.ProfileEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DbProfileRepository implements ProfileRepository {

    private final JpaProfileRepository jpa;

    public DbProfileRepository(JpaProfileRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        return jpa.findById(id.value())
                .map(ProfileEntity::toDomain);
    }

    @Override
    public Optional<Profile> findByUsername(String username) {
        return jpa.findByUsername(username)
                .map(ProfileEntity::toDomain);
    }

    @Override
    public void save(Profile profile) {
        ProfileEntity entity = ProfileEntity.fromDomain(profile);
        jpa.save(entity);
    }

    @Override
    public List<Profile> findAll() {
        return jpa.findAll().stream()
                .map(ProfileEntity::toDomain)
                .toList();
    }
}
