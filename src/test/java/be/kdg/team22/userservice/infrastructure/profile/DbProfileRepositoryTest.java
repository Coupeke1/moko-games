package be.kdg.team22.userservice.infrastructure.profile;

import be.kdg.team22.userservice.config.TestcontainersConfig;
import be.kdg.team22.userservice.infrastructure.profile.jpa.JpaProfileRepository;
import be.kdg.team22.userservice.infrastructure.profile.jpa.ProfileEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(TestcontainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DbProfileRepositoryTest {

    @Autowired
    private JpaProfileRepository repository;

    @Test
    void saveAndLoadProfile() {
        UUID id = UUID.randomUUID();
        String username = "existing-user";

        ProfileEntity entity = new ProfileEntity(id, username, Instant.now());
        repository.save(entity);

        Optional<ProfileEntity> loaded = repository.findById(id);

        assertTrue(loaded.isPresent());
    }
}