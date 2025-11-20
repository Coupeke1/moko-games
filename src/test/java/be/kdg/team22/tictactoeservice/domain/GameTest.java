package be.kdg.team22.tictactoeservice.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    private Game game;
    private final int minSize = 3;
    private final int maxSize = 10;

    @BeforeEach
    public void setup() {
        Player playerX = new Player(PlayerId.create(), PlayerRole.X);
        Player playerO = new Player(PlayerId.create(), PlayerRole.O);
        game = Game.create(minSize, maxSize, 3, List.of(playerX, playerO));
    }

    @Test
    void shouldHaveInitialStateCorrect() {
        assertNotNull(game.getId());
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        assertEquals(PlayerRole.X, game.getCurrentRole());
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
        assertEquals(PlayerRole.X, game.getCurrentRole());
        assertEquals(3, game.getBoard().getSize());
        assertNotNull(game.getBoard());
    }

    @Test
    void createShouldThrowWhenTooFewPlayers() {
        Player playerX = new Player(PlayerId.create(), PlayerRole.X);
        assertThrows(IllegalArgumentException.class, () -> Game.create(minSize, maxSize, 3, List.of(playerX)));
    }

    @Test
    void createShouldThrowWhenNonUniquePlayerIds() {
        UUID uuid = UUID.randomUUID();
        Player playerX = new Player(new PlayerId(uuid), PlayerRole.X);
        Player playerO = new Player(new PlayerId(uuid), PlayerRole.O);
        assertThrows(IllegalArgumentException.class, () -> Game.create(minSize, maxSize, 3, List.of(playerX, playerO)));
    }

    @Test
    void createShouldThrowWhenNonUniquePlayerRoles() {
        Player playerX = new Player(PlayerId.create(), PlayerRole.X);
        Player playerX2 = new Player(PlayerId.create(), PlayerRole.X);
        assertThrows(IllegalArgumentException.class, () -> Game.create(minSize, maxSize, 4, List.of(playerX, playerX2)));
    }
}
