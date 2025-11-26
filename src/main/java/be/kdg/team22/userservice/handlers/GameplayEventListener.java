package be.kdg.team22.userservice.handlers;

import be.kdg.team22.userservice.application.achievement.AchievementService;
import be.kdg.team22.userservice.config.RabbitMQTopology;
import be.kdg.team22.userservice.events.GameDrawEvent;
import be.kdg.team22.userservice.events.GameWonEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class GameplayEventListener {

    private final AchievementService achievementService;

    public GameplayEventListener(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_USER_GAMEPLAY)
    public void onGameWon(GameWonEvent event) {
        achievementService.award(event.winnerId(), "TICTACTOE_WIN");
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_USER_GAMEPLAY)
    public void onGameDraw(GameDrawEvent event) {
        event.players().forEach(p ->
                achievementService.award(p, "TICTACTOE_DRAW")
        );
    }
}
