package be.kdg.team22.userservice.profile.infrastructure.db.repositories.profile;

import be.kdg.team22.userservice.profile.domain.profile.Profile;
import be.kdg.team22.userservice.profile.domain.profile.ProfileId;
import be.kdg.team22.userservice.profile.infrastructure.db.repositories.profile.jpa.JpaProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class DbProfileRepositoryTest {

    @Autowired
    private JpaProfileRepository jpaRepo;

    @Test
    void saveAndFindWorksCorrectly() {
        DbProfileRepository repo = new DbProfileRepository(jpaRepo);

        ProfileId id = new ProfileId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        Profile profile = new Profile(id, "jan", "jan@kdg.be", Instant.now());

        repo.save(profile);

        Optional<Profile> found = repo.findById(id);

        assertThat(found).isPresent();
        found.ifPresent(value -> assertThat(value.username()).isEqualTo("jan"));
    }

    @Test
    void findAllReturnsCorrectList() {
        DbProfileRepository repo = new DbProfileRepository(jpaRepo);

        repo.save(new Profile(new ProfileId(UUID.randomUUID()), "jan", "jan@kdg.be", Instant.now()));
        repo.save(new Profile(new ProfileId(UUID.randomUUID()), "piet", "piet@kdg.be", Instant.now()));

        assertThat(repo.findAll().size()).isEqualTo(2);
    }
}
