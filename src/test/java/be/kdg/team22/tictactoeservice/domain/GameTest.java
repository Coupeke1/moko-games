package be.kdg.team22.tictactoeservice.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    private Game game;

    @BeforeEach
    public void setup() {
        game = new Game(3);
    }

    @Test
    void shouldHaveInitialStateCorrect() {
        assertNotNull(game.getId());
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        assertEquals(Player.X, game.getCurrentPlayer());
        assertEquals(3, game.getBoard().getSize());
    }

    @Test
    void resetShouldThrowIfInProgress() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, game::reset);
        assertEquals("Cannot reset GameStatus when status is IN_PROGRESS", exception.getMessage());
    }

    @Test
    void resetShouldWorkIfGameFinished() throws Exception {
        Field statusField = Game.class.getDeclaredField("status");
        statusField.setAccessible(true);
        statusField.set(game, GameStatus.WON);

        game.reset();

        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        assertEquals(Player.X, game.getCurrentPlayer());
        assertEquals(3, game.getBoard().getSize());
        assertNotNull(game.getBoard());
    }
}
