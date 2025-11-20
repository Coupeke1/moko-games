package be.kdg.team22.userservice.profile.infrastructure.db.repositories.profile;

import be.kdg.team22.userservice.config.TestcontainersConfig;
import be.kdg.team22.userservice.profile.infrastructure.db.repositories.profile.jpa.JpaProfileRepository;
import be.kdg.team22.userservice.profile.infrastructure.db.repositories.profile.jpa.ProfileEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(TestcontainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DbProfileRepositoryTest {
    @Autowired
    private JpaProfileRepository jpaRepo;

    @Test
    void saveAndLoadProfile() {
        UUID id = UUID.randomUUID();

        ProfileEntity entity = new ProfileEntity(id, "jan", "jan@kdg.be", Instant.now());
        jpaRepo.save(entity);

        Optional<ProfileEntity> loaded = jpaRepo.findById(id);

        assertTrue(loaded.isPresent());
        assertEquals("jan", loaded.get().toDomain().username());
        assertEquals("jan@kdg.be", loaded.get().toDomain().email());
    }
}
