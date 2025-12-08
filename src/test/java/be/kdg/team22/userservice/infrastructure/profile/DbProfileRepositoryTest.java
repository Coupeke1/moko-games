package be.kdg.team22.userservice.infrastructure.profile;

import be.kdg.team22.userservice.config.TestcontainersConfig;
import be.kdg.team22.userservice.infrastructure.profile.jpa.*;
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
        String email = "user@existing.com";
        String description = "Test";
        StatisticsEmbed statistics = new StatisticsEmbed(5, 50);
        ModulesEmbed modules = new ModulesEmbed(true, false);
        NotificationPreferencesEmbed preferences = new NotificationPreferencesEmbed(true, false, true, true, true);

        ProfileEntity entity = new ProfileEntity(id, username, email, description, "", statistics, modules, Instant.now(), preferences);
        repository.save(entity);

        Optional<ProfileEntity> loaded = repository.findById(id);

        assertTrue(loaded.isPresent());
    }
}