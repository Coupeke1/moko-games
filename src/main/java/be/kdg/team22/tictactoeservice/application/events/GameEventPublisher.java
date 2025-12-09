package be.kdg.team22.tictactoeservice.application.events;

import be.kdg.team22.tictactoeservice.domain.events.GameAchievementEvent;
import be.kdg.team22.tictactoeservice.domain.events.GameEndedEvent;

public interface GameEventPublisher {
    void publishAchievement(GameAchievementEvent event);

    void publishGameEnded(GameEndedEvent event);
}