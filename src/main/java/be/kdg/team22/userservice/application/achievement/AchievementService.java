package be.kdg.team22.userservice.application.achievement;

import be.kdg.team22.userservice.domain.achievement.Achievement;
import be.kdg.team22.userservice.domain.achievement.AchievementCode;
import be.kdg.team22.userservice.domain.achievement.AchievementId;
import be.kdg.team22.userservice.domain.achievement.AchievementRepository;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AchievementService {

    private final AchievementRepository achievements;

    public AchievementService(AchievementRepository achievements) {
        this.achievements = achievements;
    }

    public void award(final UUID userId, final String code) {
        award(userId, null, code);
    }

    public void award(final UUID userId, final UUID gameId, final String code) {
        ProfileId profileId = new ProfileId(userId);
        AchievementCode achievementCode = new AchievementCode(code);

        if (achievements.existsByProfileAndCode(profileId, achievementCode)) {
            return;
        }

        Achievement achievement = new Achievement(
                AchievementId.create(),
                profileId,
                gameId,
                achievementCode,
                Instant.now()
        );

        achievements.save(achievement);
    }

    public List<Achievement> findByProfile(ProfileId profileId) {
        return achievements.findByProfile(profileId);
    }
}