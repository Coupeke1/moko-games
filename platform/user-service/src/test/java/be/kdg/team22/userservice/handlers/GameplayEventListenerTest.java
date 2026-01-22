package be.kdg.team22.userservice.handlers;

import be.kdg.team22.userservice.application.achievement.AchievementService;
import be.kdg.team22.userservice.domain.achievement.AchievementKey;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.events.GameAchievementEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class GameplayEventListenerTest {

    private final AchievementService achievementService = mock(AchievementService.class);
    private final GameplayEventListener listener = new GameplayEventListener(achievementService);

    @Test
    @DisplayName("onGameWon → awards win achievements to winner")
    void onGameWon_awardsWinner() {
        UUID winnerId = UUID.randomUUID();
        String gameName = "TicTacToe";
        UUID gameId = UUID.randomUUID();
        GameAchievementEvent event = new GameAchievementEvent("TICTACTOE_WIN", gameName, gameId, winnerId, Instant.now());

        listener.onGameAchievement(event);

        verify(achievementService).award(new ProfileId(winnerId), gameName, new AchievementKey("TICTACTOE_WIN"));
    }

    @Test
    @DisplayName("onGameDraw → awards draw achievements to player")
    void onGameDraw_awardsPlayer() {
        UUID playerId = UUID.randomUUID();
        String gameName = "TicTacToe";
        UUID gameId = UUID.randomUUID();
        GameAchievementEvent event = new GameAchievementEvent("TICTACTOE_DRAW", gameName, gameId, playerId, Instant.now());

        listener.onGameAchievement(event);

        verify(achievementService).award(new ProfileId(playerId), gameName, new AchievementKey("TICTACTOE_DRAW"));
    }
}