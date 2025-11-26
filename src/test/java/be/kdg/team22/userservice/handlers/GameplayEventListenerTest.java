package be.kdg.team22.userservice.handlers;

import be.kdg.team22.userservice.application.achievement.AchievementService;
import be.kdg.team22.userservice.events.GameDrawEvent;
import be.kdg.team22.userservice.events.GameWonEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class GameplayEventListenerTest {

    private final AchievementService achievementService = mock(AchievementService.class);
    private final GameplayEventListener listener = new GameplayEventListener(achievementService);

    @Test
    @DisplayName("onGameWon → awards win achievement to winner")
    void onGameWon_awardsWinner() {
        UUID winnerId = UUID.randomUUID();
        GameWonEvent event = new GameWonEvent(UUID.randomUUID(), winnerId, Instant.now());

        listener.onGameWon(event);

        verify(achievementService).award(winnerId, "TICTACTOE_WIN");
    }

    @Test
    @DisplayName("onGameDraw → awards draw achievement to all players")
    void onGameDraw_awardsAllPlayers() {
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        GameDrawEvent event = new GameDrawEvent(UUID.randomUUID(), List.of(p1, p2), Instant.now());

        listener.onGameDraw(event);

        verify(achievementService).award(p1, "TICTACTOE_DRAW");
        verify(achievementService).award(p2, "TICTACTOE_DRAW");
    }

    @Test
    @DisplayName("onGameDraw → no players = no awards")
    void onGameDraw_emptyList() {
        GameDrawEvent event = new GameDrawEvent(UUID.randomUUID(), List.of(), Instant.now());

        listener.onGameDraw(event);

        verifyNoInteractions(achievementService);
    }
}