package be.kdg.team22.userservice.application.achievement;

import be.kdg.team22.userservice.application.profile.ProfileService;
import be.kdg.team22.userservice.domain.achievement.*;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.events.AchievementUnlockedEvent;
import be.kdg.team22.userservice.infrastructure.messaging.AchievementEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AchievementService {

    private final AchievementRepository achievements;
    private final AchievementEventPublisher eventPublisher;
    private final ProfileService profileService;

    public AchievementService(final AchievementRepository achievements, final AchievementEventPublisher eventPublisher, final ProfileService profileService) {
        this.achievements = achievements;
        this.eventPublisher = eventPublisher;
        this.profileService = profileService;
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

        int levels = AchievementMetadata.getLevels(code);
        profileService.addLevels(profileId, levels);

        AchievementUnlockedEvent event = new AchievementUnlockedEvent(
                userId,
                achievement.id().value(),
                AchievementMetadata.getName(code),
                AchievementMetadata.getDescription(code)
        );
        eventPublisher.publishAchievementUnlocked(event);
    }

    public List<Achievement> findByProfile(final ProfileId profileId) {
        return achievements.findByProfile(profileId);
    }
}