package be.kdg.team22.userservice.handlers;

import be.kdg.team22.userservice.application.achievement.AchievementService;
import be.kdg.team22.userservice.events.GameAchievementEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;

class GameplayEventListenerTest {

    private final AchievementService achievementService = mock(AchievementService.class);
    private final GameplayEventListener listener = new GameplayEventListener(achievementService);

    @Test
    @DisplayName("onGameWon → awards win achievement to winner")
    void onGameWon_awardsWinner() {
        UUID winnerId = UUID.randomUUID();
        GameAchievementEvent event = new GameAchievementEvent("TICTACTOE_WIN", UUID.randomUUID(), winnerId, Instant.now());

        listener.onGameAchievement(event);

        verify(achievementService).award(winnerId, "TICTACTOE_WIN");
    }

    @Test
    @DisplayName("onGameDraw → awards draw achievement to player")
    void onGameDraw_awardsPlayer() {
        UUID playerId = UUID.randomUUID();
        GameAchievementEvent event = new GameAchievementEvent("TICTACTOE_DRAW", UUID.randomUUID(), playerId, Instant.now());

        listener.onGameAchievement(event);

        verify(achievementService).award(playerId, "TICTACTOE_DRAW");
    }
}