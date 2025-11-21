package be.kdg.team22.tictactoeservice.domain;

import be.kdg.team22.tictactoeservice.domain.game.Game;
import be.kdg.team22.tictactoeservice.domain.game.GameStatus;
import be.kdg.team22.tictactoeservice.domain.game.exceptions.GameResetException;
import be.kdg.team22.tictactoeservice.domain.game.exceptions.GameSizeException;
import be.kdg.team22.tictactoeservice.domain.game.exceptions.PlayerRolesException;
import be.kdg.team22.tictactoeservice.domain.game.exceptions.UniquePlayersException;
import be.kdg.team22.tictactoeservice.domain.player.Player;
import be.kdg.team22.tictactoeservice.domain.player.PlayerId;
import be.kdg.team22.tictactoeservice.domain.player.PlayerRole;
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
        assertNotNull(game.id());
        assertEquals(GameStatus.IN_PROGRESS, game.status());
        assertEquals(PlayerRole.X, game.currentRole());
        assertEquals(3, game.board().size());
    }

    @Test
    void resetShouldThrowIfInProgress() {
        assertThrows(GameResetException.class, () -> game.reset());
    }

    @Test
    void resetShouldWorkIfGameFinished() throws Exception {
        Field statusField = Game.class.getDeclaredField("status");
        statusField.setAccessible(true);
        statusField.set(game, GameStatus.WON);

        game.reset();

        assertEquals(GameStatus.IN_PROGRESS, game.status());
        assertEquals(PlayerRole.X, game.currentRole());
        assertEquals(3, game.board().size());
        assertNotNull(game.board());
    }

    @Test
    void createShouldThrowWhenTooFewPlayers() {
        Player playerX = new Player(PlayerId.create(), PlayerRole.X);
        assertThrows(GameSizeException.class, () -> Game.create(minSize, maxSize, 3, List.of(playerX)));
    }

    @Test
    void createShouldThrowWhenNonUniquePlayerIds() {
        UUID uuid = UUID.randomUUID();
        Player playerX = new Player(new PlayerId(uuid), PlayerRole.X);
        Player playerO = new Player(new PlayerId(uuid), PlayerRole.O);
        assertThrows(UniquePlayersException.class, () -> Game.create(minSize, maxSize, 3, List.of(playerX, playerO)));
    }

    @Test
    void createShouldThrowWhenNonUniquePlayerRoles() {
        Player playerX = new Player(PlayerId.create(), PlayerRole.X);
        Player playerX2 = new Player(PlayerId.create(), PlayerRole.X);
        assertThrows(PlayerRolesException.class, () -> Game.create(minSize, maxSize, 4, List.of(playerX, playerX2)));
    }

    @Test
    void initialRoleShouldBeFirstPlayersRole() {
        assertEquals(PlayerRole.X, game.currentRole());
        assertEquals(PlayerRole.X, game.players().first().role());
    }

    @Test
    void nextPlayerShouldGoToNextRole() {
        assertEquals(PlayerRole.X, game.currentRole());
        assertEquals(PlayerRole.O, game.nextPlayer().role());
        assertEquals(PlayerRole.O, game.currentRole());
    }

    @Test
    void nextPlayerShouldWrapAroundToFirstPlayer() throws NoSuchFieldException, IllegalAccessException {
        Field currentRoleField = Game.class.getDeclaredField("currentRole");
        currentRoleField.setAccessible(true);
        currentRoleField.set(game, PlayerRole.O);


        assertEquals(PlayerRole.O, game.currentRole());
        assertEquals(PlayerRole.X, game.nextPlayer().role());
        assertEquals(PlayerRole.X, game.currentRole());
    }
}
