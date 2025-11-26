package be.kdg.team22.userservice.infrastructure.achievement;

import be.kdg.team22.userservice.domain.achievement.Achievement;
import be.kdg.team22.userservice.domain.achievement.AchievementCode;
import be.kdg.team22.userservice.domain.achievement.AchievementRepository;
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
    public Optional<Achievement> findByProfileAndCode(ProfileId profileId, AchievementCode code) {
        return jpa.findByProfileIdAndCode(profileId.value(), code.value())
                .map(AchievementEntity::toDomain);
    }

    @Override
    public List<Achievement> findByProfile(ProfileId profileId) {
        return jpa.findByProfileId(profileId.value()).stream()
                .map(AchievementEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsByProfileAndCode(ProfileId profileId, AchievementCode code) {
        return jpa.existsByProfileIdAndCode(profileId.value(), code.value());
    }

    @Override
    public long countByProfile(ProfileId profileId) {
        return jpa.countByProfileId(profileId.value());
    }

    @Override
    public long countByProfileAndGame(ProfileId profileId, UUID gameId) {
        return jpa.countByProfileIdAndGameId(profileId.value(), gameId);
    }
}
