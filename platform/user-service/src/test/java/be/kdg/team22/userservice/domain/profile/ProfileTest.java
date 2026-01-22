package be.kdg.team22.userservice.domain.profile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileTest {

    private final ProfileId id = new ProfileId(UUID.randomUUID());
    private final ProfileName username = new ProfileName("testuser");
    private final ProfileEmail email = new ProfileEmail("test@example.com");
    private final String description = "Test description";
    private final String image = "https://example.com/image.png";

    @Test
    @DisplayName("addLevels increases profile level correctly")
    void addLevels_increasesLevel() {
        Profile profile = new Profile(id, username, email, description, image);

        assertThat(profile.statistics().level()).isEqualTo(0);

        profile.addLevels(5);

        assertThat(profile.statistics().level()).isEqualTo(5);
    }

    @Test
    @DisplayName("addLevels can be called multiple times")
    void addLevels_multipleTimes_accumulatesLevels() {
        Profile profile = new Profile(id, username, email, description, image);

        profile.addLevels(3);
        profile.addLevels(2);
        profile.addLevels(5);

        assertThat(profile.statistics().level()).isEqualTo(10);
    }

    @Test
    @DisplayName("addLevels preserves playTime")
    void addLevels_preservesPlayTime() {
        Statistics initialStats = new Statistics(0, 100);
        Profile profile = new Profile(id, username, email, description, image, initialStats, new Modules(false, false), new Notifications(true, true, true, true, true), java.time.Instant.now());

        profile.addLevels(5);

        assertThat(profile.statistics().level()).isEqualTo(5);
        assertThat(profile.statistics().playTime()).isEqualTo(100);
    }
}