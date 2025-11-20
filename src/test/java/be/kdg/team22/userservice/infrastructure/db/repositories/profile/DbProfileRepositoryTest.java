package be.kdg.team22.userservice.infrastructure.db.repositories.profile;

import be.kdg.team22.userservice.infrastructure.db.repositories.jpa.JpaProfileRepository;
import be.kdg.team22.userservice.infrastructure.db.repositories.jpa.ProfileEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DbProfileRepositoryTest {
    @Autowired
    private JpaProfileRepository repository;

    @Test
    void saveAndLoadProfile() {
        UUID id = UUID.randomUUID();

        ProfileEntity entity = new ProfileEntity(id, "jan", "jan@kdg.be", Instant.now());
        repository.save(entity);

        Optional<ProfileEntity> loaded = repository.findById(id);

        assertTrue(loaded.isPresent());
        assertEquals("jan", loaded.get().toDomain().username());
        assertEquals("jan@kdg.be", loaded.get().toDomain().email());
    }
}