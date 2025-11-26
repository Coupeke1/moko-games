package be.kdg.team22.userservice.infrastructure.achievement.jpa;

import be.kdg.team22.userservice.domain.achievement.Achievement;
import be.kdg.team22.userservice.domain.achievement.AchievementCode;
import be.kdg.team22.userservice.domain.achievement.AchievementId;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AchievementEntityTest {

    private final UUID id = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private final UUID profileId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private final UUID gameId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
    private final String code = "win.first";
    private final Instant unlockedAt = Instant.parse("2024-01-01T10:00:00Z");

    @Test
    @DisplayName("fromDomain → maps ALL fields correctly")
    void fromDomain_mapsCorrectly() {
        Achievement domain = new Achievement(
                new AchievementId(id),
                new ProfileId(profileId),
                gameId,
                new AchievementCode(code),
                unlockedAt
        );

        AchievementEntity entity = AchievementEntity.fromDomain(domain);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getProfileId()).isEqualTo(profileId);
        assertThat(entity.getGameId()).isEqualTo(gameId);
        assertThat(entity.getCode()).isEqualTo(code);
        assertThat(entity.getUnlockedAt()).isEqualTo(unlockedAt);
    }

    @Test
    @DisplayName("toDomain → maps entity → domain correctly")
    void toDomain_mapsCorrectly() {
        AchievementEntity entity =
                new AchievementEntity(id, profileId, gameId, code, unlockedAt);

        Achievement domain = entity.toDomain();

        assertThat(domain.id().value()).isEqualTo(id);
        assertThat(domain.profileId().value()).isEqualTo(profileId);
        assertThat(domain.gameId()).isEqualTo(gameId);
        assertThat(domain.code().value()).isEqualTo(code);
        assertThat(domain.unlockedAt()).isEqualTo(unlockedAt);
    }

    @Test
    @DisplayName("Roundtrip: domain → entity → domain is identical")
    void roundTrip_isConsistent() {
        Achievement original = new Achievement(
                new AchievementId(id),
                new ProfileId(profileId),
                gameId,
                new AchievementCode(code),
                unlockedAt
        );

        AchievementEntity entity = AchievementEntity.fromDomain(original);
        Achievement mapped = entity.toDomain();

        assertThat(mapped).isEqualTo(original);
    }

    @Test
    @DisplayName("Constructor sets all fields")
    void constructor_setsFieldsCorrectly() {
        AchievementEntity entity =
                new AchievementEntity(id, profileId, gameId, code, unlockedAt);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getProfileId()).isEqualTo(profileId);
        assertThat(entity.getGameId()).isEqualTo(gameId);
        assertThat(entity.getCode()).isEqualTo(code);
        assertThat(entity.getUnlockedAt()).isEqualTo(unlockedAt);
    }

    @Test
    @DisplayName("JPA default constructor leaves fields null")
    void jpaConstructor_defaults() {
        AchievementEntity entity = new AchievementEntity();

        assertThat(entity.getId()).isNull();
        assertThat(entity.getProfileId()).isNull();
        assertThat(entity.getGameId()).isNull();
        assertThat(entity.getCode()).isNull();
        assertThat(entity.getUnlockedAt()).isNull();
    }
}