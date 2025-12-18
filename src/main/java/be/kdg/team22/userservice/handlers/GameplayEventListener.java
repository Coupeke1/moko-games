package be.kdg.team22.userservice.handlers;

import be.kdg.team22.userservice.application.achievement.AchievementService;
import be.kdg.team22.userservice.config.RabbitMQTopology;
import be.kdg.team22.userservice.domain.achievement.AchievementKey;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.events.GameAchievementEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class GameplayEventListener {
    private final AchievementService achievementService;

    public GameplayEventListener(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_USER_GAMEPLAY)
    public void onGameAchievement(GameAchievementEvent event) {
        achievementService.award(new ProfileId(event.playerId()), event.gameName(), new AchievementKey(event.achievementCode()));
    }
}
