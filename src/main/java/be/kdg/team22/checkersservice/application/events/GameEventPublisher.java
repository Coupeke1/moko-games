package be.kdg.team22.checkersservice.application.events;

import be.kdg.team22.checkersservice.domain.events.*;

public interface GameEventPublisher {
    void publishAchievement(GameAchievementEvent event);
}
