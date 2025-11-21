package be.kdg.team22.tictactoeservice.domain;

import be.kdg.team22.tictactoeservice.domain.game.Game;
import be.kdg.team22.tictactoeservice.domain.game.GameStatus;
import be.kdg.team22.tictactoeservice.domain.game.Move;
import be.kdg.team22.tictactoeservice.domain.game.exceptions.*;
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

    @Test
    void roleOfPlayerShouldReturnCorrectRoleWhenPresent() {
        Player playerO = game.players().stream().filter(p -> p.role().equals(PlayerRole.O)).findFirst().get();

        assertEquals(playerO.role(), game.roleOfPlayer(playerO.id()));
        assertEquals(PlayerRole.O, game.roleOfPlayer(playerO.id()));
    }

    @Test
    void roleOfPlayerShouldThrowWhenNotPresent() {
        assertThrows(NotFoundException.class, () -> game.roleOfPlayer(PlayerId.create()));
    }

    @Test
    void requestMoveShouldThrowWhenNotInProgress() throws NoSuchFieldException, IllegalAccessException {
        Field statusField = Game.class.getDeclaredField("status");
        statusField.setAccessible(true);
        statusField.set(game, GameStatus.WON);

        assertThrows(GameNotInProgressException.class, () -> game.requestMove(new Move(game.id(), game.players().first().id(), 1, 1)));
    }

    @Test
    void requestMoveShouldThrowWhenNotPlayersTurn() {
        assertThrows(NotPlayersTurnException.class, () -> game.requestMove(new Move(game.id(), game.players().stream().toList().get(1).id(), 1, 1)));
    }

    @Test
    void requestMoveShouldThrowWhenInvalidCellPos() {
        assertThrows(InvalidCellException.class, () -> game.requestMove(new Move(game.id(), game.players().first().id(), -1, 1)));
        assertThrows(InvalidCellException.class, () -> game.requestMove(new Move(game.id(), game.players().first().id(), 1, -1)));
        assertThrows(InvalidCellException.class, () -> game.requestMove(new Move(game.id(), game.players().first().id(), game.board().size(), 1)));
        assertThrows(InvalidCellException.class, () -> game.requestMove(new Move(game.id(), game.players().first().id(), 1, game.board().size())));
        assertThrows(InvalidCellException.class, () -> game.requestMove(new Move(game.id(), game.players().first().id(), game.board().size() + 1, 1)));
        assertThrows(InvalidCellException.class, () -> game.requestMove(new Move(game.id(), game.players().first().id(), 1, game.board().size() + 1)));
    }

    @Test
    void requestMoveShouldThrowWhenCellAlreadyOccupied() {
        game.requestMove(new Move(game.id(), game.players().first().id(), 1, 1));
        assertThrows(CellOccupiedException.class, () -> game.requestMove(new Move(game.id(), game.currentPlayer().id(), 1, 1)));
    }

    @Test
    void moveHistoryShouldBeSaved() {
        PlayerId playerXId = game.players().first().id();
        PlayerId playerOId = game.players().stream().toList().get(1).id();
        Move move = new Move(game.id(), playerXId, 1, 1);
        game.requestMove(move);
        game.requestMove(new Move(game.id(), playerOId, 1, 2));
        game.requestMove(new Move(game.id(), playerXId, 2, 1));
        assertEquals(2, game.moveHistory().size());
        assertEquals(2, game.moveHistory().get(playerXId).size());
        assertEquals(1, game.moveHistory().get(playerOId).size());
        assertEquals(move, game.moveHistory().get(playerXId).getFirst());
    }
}
