package be.kdg.team22.userservice.infrastructure.achievement;

import be.kdg.team22.userservice.domain.achievement.Achievement;
import be.kdg.team22.userservice.domain.achievement.AchievementKey;
import be.kdg.team22.userservice.domain.achievement.AchievementRepository;
import be.kdg.team22.userservice.domain.library.GameId;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.infrastructure.achievement.jpa.AchievementEntity;
import be.kdg.team22.userservice.infrastructure.achievement.jpa.JpaAchievementRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class DbAchievementRepository implements AchievementRepository {

    private final JpaAchievementRepository jpa;

    public DbAchievementRepository(JpaAchievementRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Achievement achievement) {
        jpa.save(AchievementEntity.fromDomain(achievement));
    }

    @Override
    public Optional<Achievement> findByProfileGameAndKey(ProfileId profileId, GameId gameId, AchievementKey key) {
        return jpa.findByIdProfileIdAndIdGameIdAndIdKey(profileId.value(), gameId.value(), key.key())
                .map(AchievementEntity::toDomain);
    }

    @Override
    public List<Achievement> findByProfile(ProfileId profileId) {
        return jpa.findByIdProfileId(profileId.value()).stream()
                .map(AchievementEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsByProfileGameAndKey(ProfileId profileId, GameId gameId, AchievementKey key) {
        return jpa.existsByIdProfileIdAndIdGameIdAndIdKey(profileId.value(), gameId.value(), key.key());
    }

    @Override
    public long countByProfile(ProfileId profileId) {
        return jpa.countByIdProfileId(profileId.value());
    }

    @Override
    public long countByProfileAndGame(ProfileId profileId, UUID gameId) {
        return jpa.countByIdProfileIdAndIdGameId(profileId.value(), gameId);
    }
}
