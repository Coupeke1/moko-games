package be.kdg.team22.userservice.application.achievement;

import be.kdg.team22.userservice.api.achievement.models.AchievementModel;
import be.kdg.team22.userservice.application.profile.ProfileService;
import be.kdg.team22.userservice.domain.achievement.Achievement;
import be.kdg.team22.userservice.domain.achievement.AchievementKey;
import be.kdg.team22.userservice.domain.achievement.AchievementRepository;
import be.kdg.team22.userservice.domain.library.GameId;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.events.AchievementUnlockedEvent;
import be.kdg.team22.userservice.infrastructure.games.AchievementDetailsResponse;
import be.kdg.team22.userservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.userservice.infrastructure.games.GameDetailsResponse;
import be.kdg.team22.userservice.infrastructure.messaging.AchievementEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AchievementService {
    private final AchievementRepository achievements;
    private final AchievementEventPublisher eventPublisher;
    private final ProfileService profileService;
    private final ExternalGamesRepository gamesRepository;

    public AchievementService(final AchievementRepository achievements, final AchievementEventPublisher eventPublisher, final ProfileService profileService, final ExternalGamesRepository gameRepo) {
        this.achievements = achievements;
        this.eventPublisher = eventPublisher;
        this.profileService = profileService;
        this.gamesRepository = gameRepo;
    }

    public void award(final ProfileId userId, final String gameName, final AchievementKey key) {
        GameDetailsResponse game = gamesRepository.getGame(gameName);
        GameId gameId = GameId.from(game.id());

        if (achievements.existsByProfileGameAndKey(userId, gameId, key)) {
            return;
        }

        Achievement achievement = Achievement.create(userId, gameId, key);
        achievements.save(achievement);

        AchievementDetailsResponse achievementDetails = gamesRepository.getAchievementDetails(gameId, key);
        profileService.addLevels(userId, achievementDetails.levels());

        AchievementUnlockedEvent event = new AchievementUnlockedEvent(userId.value(), gameId.value(), key.key(), achievementDetails.name(), achievementDetails.description());
        eventPublisher.publishAchievementUnlocked(event);
    }

    public List<Achievement> findByProfile(final ProfileId profileId) {
        return achievements.findByProfile(profileId);
    }

    public List<AchievementModel> findAchievementsByProfile(final ProfileId profileId) {
        return achievements.findByProfile(profileId).stream().map(a -> {
            GameDetailsResponse game = gamesRepository.getGame(a.gameId().value());
            AchievementDetailsResponse achievement = gamesRepository.getAchievementDetails(a.gameId(), a.key());
            return AchievementModel.from(a, achievement, game);
        }).toList();
    }

    public List<AchievementModel> findAchievementsByProfileAndGame(final ProfileId profileId, final GameId gameId) {
        return achievements.findByProfile(profileId).stream().map(a -> {
            GameDetailsResponse game = gamesRepository.getGame(a.gameId().value());
            AchievementDetailsResponse achievement = gamesRepository.getAchievementDetails(a.gameId(), a.key());
            return AchievementModel.from(a, achievement, game);
        }).filter(achievement -> achievement.gameId().equals(gameId.value())).toList();
    }
}