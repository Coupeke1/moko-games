package be.kdg.team22.userservice.handlers;

import be.kdg.team22.userservice.application.achievement.AchievementService;
import be.kdg.team22.userservice.config.RabbitMQTopology;
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
        achievementService.award(event.playerId(), event.achievementCode());
    }
}
