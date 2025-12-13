package be.kdg.team22.userservice.domain.achievement;

import be.kdg.team22.userservice.domain.achievement.exceptions.AchievementException;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AchievementTest {

    private final AchievementId id =
            new AchievementId(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
    private final ProfileId profileId =
            new ProfileId(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"));
    private final UUID gameId =
            UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
    private final AchievementCode code = new AchievementCode("win.first");
    private final Instant unlockedAt = Instant.parse("2024-01-01T10:00:00Z");

    @Test
    @DisplayName("Valid achievements → created successfully")
    void validAchievement() {
        Achievement a = new Achievement(id, profileId, gameId, code, unlockedAt);

        assertThat(a.id()).isEqualTo(id);
        assertThat(a.profileId()).isEqualTo(profileId);
        assertThat(a.gameId()).isEqualTo(gameId);
        assertThat(a.code()).isEqualTo(code);
        assertThat(a.unlockedAt()).isEqualTo(unlockedAt);
    }

    @Test
    @DisplayName("profileId cannot be null")
    void nullProfileId_throws() {
        assertThatThrownBy(() ->
                new Achievement(id, null, gameId, code, unlockedAt)
        ).isInstanceOf(AchievementException.class)
                .hasMessage("profileId cannot be null");
    }

    @Test
    @DisplayName("code cannot be null")
    void nullCode_throws() {
        assertThatThrownBy(() ->
                new Achievement(id, profileId, gameId, null, unlockedAt)
        ).isInstanceOf(AchievementException.class)
                .hasMessage("achievementCode cannot be null or blank");
    }

    @Test
    @DisplayName("unlockedAt cannot be null")
    void nullUnlockedAt_throws() {
        assertThatThrownBy(() ->
                new Achievement(id, profileId, gameId, code, null)
        ).isInstanceOf(AchievementException.class)
                .hasMessage("unlockedAt cannot be null");
    }
}