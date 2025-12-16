package be.kdg.team22.userservice.domain.achievement;

import be.kdg.team22.userservice.domain.achievement.exceptions.AchievementException;
import be.kdg.team22.userservice.domain.library.GameId;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AchievementTest {

    private final ProfileId profileId =
            new ProfileId(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"));
    private final GameId gameId =
            new GameId(UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"));
    private final AchievementKey key = new AchievementKey("WIN_FIRST");
    private final Instant unlockedAt = Instant.parse("2024-01-01T10:00:00Z");

    @Test
    @DisplayName("Valid achievements → created successfully")
    void validAchievement() {
        Achievement a = new Achievement(profileId, gameId, key, unlockedAt);

        assertThat(a.profileId()).isEqualTo(profileId);
        assertThat(a.gameId()).isEqualTo(gameId);
        assertThat(a.key()).isEqualTo(key);
        assertThat(a.unlockedAt()).isEqualTo(unlockedAt);
    }

    @Test
    @DisplayName("profileId cannot be null")
    void nullProfileId_throws() {
        assertThatThrownBy(() ->
                new Achievement(null, gameId, key, unlockedAt)
        ).isInstanceOf(AchievementException.class);
    }

    @Test
    @DisplayName("code cannot be null")
    void nullCode_throws() {
        assertThatThrownBy(() ->
                new Achievement(profileId, gameId, null, unlockedAt)
        ).isInstanceOf(AchievementException.class);
    }

    @Test
    @DisplayName("unlockedAt cannot be null")
    void nullUnlockedAt_throws() {
        assertThatThrownBy(() ->
                new Achievement(profileId, gameId, key, null)
        ).isInstanceOf(AchievementException.class);
    }
}