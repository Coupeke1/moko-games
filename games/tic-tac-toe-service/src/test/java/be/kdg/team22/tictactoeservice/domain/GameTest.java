package be.kdg.team22.tictactoeservice.domain;

import be.kdg.team22.tictactoeservice.domain.game.Board;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    private Game game;
    private final int minSize = 3;
    private final int maxSize = 10;
    private final int boardSize = 3;

    @BeforeEach
    public void setup() {
        Player playerX = new Player(PlayerId.create(), PlayerRole.X, false);
        Player playerO = new Player(PlayerId.create(), PlayerRole.O, false);
        game = Game.create(minSize, maxSize, 3,
                List.of(playerX.id(), playerO.id()), false);
    }

    @Test
    void shouldHaveInitialStateCorrect() {
        assertNotNull(game.id());
        assertEquals(GameStatus.IN_PROGRESS, game.status());
        assertEquals(PlayerRole.X, game.currentRole());
        assertEquals(boardSize, game.board().size());
    }

    @Test
    void createShouldThrowWhenTooFewPlayers() {
        PlayerId playerId = PlayerId.create();
        assertThrows(GameSizeException.class, () ->
                Game.create(minSize, maxSize, 3,
                        List.of(playerId), false));
    }

    @Test
    void createShouldThrowWhenTooManyPlayers() {
        List<PlayerId> playerIds = new ArrayList<>();
        for (int i = 0; i <= PlayerRole.values().length; i++) {
            playerIds.add(PlayerId.create());
        }
        assertThrows(GameSizeException.class, () ->
                Game.create(minSize, maxSize, 3,
                        playerIds, false));
    }

    @Test
    void createShouldThrowWhenNonUniquePlayerIds() {
        UUID uuid = UUID.randomUUID();
        PlayerId playerX = new PlayerId(uuid);
        PlayerId playerO = new PlayerId(uuid);
        assertThrows(UniquePlayersException.class, () ->
                Game.create(minSize, maxSize, 3,
                        List.of(playerX, playerO), false));
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

        assertThrows(GameNotInProgressException.class, () -> game.requestMove(new Move(game.players().first().id(), 1, 1)));
    }

    @Test
    void requestMoveShouldThrowWhenNotPlayersTurn() {
        assertThrows(NotPlayersTurnException.class, () -> game.requestMove(new Move(game.players().stream().toList().get(1).id(), 1, 1)));
    }

    @Test
    void requestMoveShouldThrowWhenInvalidCellPos() {
        assertThrows(InvalidCellException.class, () -> game.requestMove(new Move(game.players().first().id(), -1, 1)));
        assertThrows(InvalidCellException.class, () -> game.requestMove(new Move(game.players().first().id(), 1, -1)));
        assertThrows(InvalidCellException.class, () -> game.requestMove(new Move(game.players().first().id(), game.board().size(), 1)));
        assertThrows(InvalidCellException.class, () -> game.requestMove(new Move(game.players().first().id(), 1, game.board().size())));
        assertThrows(InvalidCellException.class, () -> game.requestMove(new Move(game.players().first().id(), game.board().size() + 1, 1)));
        assertThrows(InvalidCellException.class, () -> game.requestMove(new Move(game.players().first().id(), 1, game.board().size() + 1)));
    }

    @Test
    void requestMoveShouldThrowWhenCellAlreadyOccupied() {
        game.requestMove(new Move(game.players().first().id(), 1, 1));
        assertThrows(CellOccupiedException.class, () -> game.requestMove(new Move(game.currentPlayer().id(), 1, 1)));
    }

    @Test
    void moveHistoryShouldBeSaved() {
        PlayerId playerXId = game.players().first().id();
        PlayerId playerOId = game.players().stream().toList().get(1).id();
        Move move = new Move(playerXId, 1, 1);
        game.requestMove(move);
        game.requestMove(new Move(playerOId, 1, 2));
        game.requestMove(new Move(playerXId, 2, 1));
        assertEquals(2, game.moveHistory().size());
        assertEquals(2, game.moveHistory().get(playerXId).size());
        assertEquals(1, game.moveHistory().get(playerOId).size());
        assertEquals(move, game.moveHistory().get(playerXId).getFirst());
    }

    @Test
    void requestMoveShouldDeclareTieWhenBoardIsFull() throws NoSuchFieldException, IllegalAccessException {
        Board board = game.board();
        PlayerRole[][] fullGrid = new PlayerRole[board.size()][board.size()];

        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < board.size(); j++) {
                if (i == board.size() - 1 && j == board.size() - 1) {
                    fullGrid[i][j] = null;
                } else {
                    if ((i + j) % 2 == 0) {
                        fullGrid[i][j] = game.roleOfPlayer(game.players().stream().toList().get(1).id());
                    } else {
                        fullGrid[i][j] = game.roleOfPlayer(game.players().first().id());
                    }
                }
            }
        }

        Field gridField = Board.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        gridField.set(board, fullGrid);

        Move lastMove = new Move(game.players().first().id(), board.size() - 1, board.size() - 1);

        game.requestMove(lastMove);

        assertEquals(GameStatus.TIE, game.status());
        assertNull(game.winner());
    }

    @Test
    void requestMoveShouldDeclareWinRight() throws NoSuchFieldException, IllegalAccessException {
        Field currentRoleField = Game.class.getDeclaredField("currentRole");
        currentRoleField.setAccessible(true);
        currentRoleField.set(game, PlayerRole.O);

        Board board = game.board();
        PlayerRole[][] grid = new PlayerRole[board.size()][board.size()];
        grid[0][0] = game.currentRole();
        grid[0][1] = game.currentRole();
        grid[0][2] = null;

        Field gridField = Board.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        gridField.set(board, grid);

        Move winningMove = new Move(game.currentPlayer().id(), 0, 2);
        game.requestMove(winningMove);

        assertEquals(GameStatus.WON, game.status());
        assertEquals(game.winner(), game.players().stream().toList().get(1).id());
    }

    @Test
    void requestMoveShouldDeclareWinLeft() throws NoSuchFieldException, IllegalAccessException {
        Board board = game.board();
        PlayerRole[][] grid = new PlayerRole[board.size()][board.size()];
        grid[0][2] = game.currentRole();
        grid[0][1] = game.currentRole();
        grid[0][0] = null;

        Field gridField = Board.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        gridField.set(board, grid);

        Move winningMove = new Move(game.currentPlayer().id(), 0, 0);
        game.requestMove(winningMove);

        assertEquals(GameStatus.WON, game.status());
        assertEquals(game.winner(), game.players().first().id());
    }

    @Test
    void requestMoveShouldDeclareWinDown() throws NoSuchFieldException, IllegalAccessException {
        Board board = game.board();
        PlayerRole[][] grid = new PlayerRole[board.size()][board.size()];
        grid[0][0] = game.currentRole();
        grid[1][0] = game.currentRole();
        grid[2][0] = null;

        Field gridField = Board.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        gridField.set(board, grid);

        Move winningMove = new Move(game.currentPlayer().id(), 2, 0);
        game.requestMove(winningMove);

        assertEquals(GameStatus.WON, game.status());
        assertEquals(game.winner(), game.players().first().id());
    }

    @Test
    void requestMoveShouldDeclareWinUp() throws NoSuchFieldException, IllegalAccessException {
        Board board = game.board();
        PlayerRole[][] grid = new PlayerRole[board.size()][board.size()];
        grid[2][0] = game.currentRole();
        grid[1][0] = game.currentRole();
        grid[0][0] = null;

        Field gridField = Board.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        gridField.set(board, grid);

        Move winningMove = new Move(game.currentPlayer().id(), 0, 0);
        game.requestMove(winningMove);

        assertEquals(GameStatus.WON, game.status());
        assertEquals(game.winner(), game.players().first().id());
    }

    @Test
    void requestMoveShouldDeclareWinDiagonal() throws NoSuchFieldException, IllegalAccessException {
        Board board = game.board();
        PlayerRole[][] grid = new PlayerRole[board.size()][board.size()];
        grid[0][0] = game.currentRole();
        grid[1][1] = game.currentRole();
        grid[2][2] = null;

        Field gridField = Board.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        gridField.set(board, grid);

        Move winningMove = new Move(game.currentPlayer().id(), 2, 2);
        game.requestMove(winningMove);

        assertEquals(GameStatus.WON, game.status());
        assertEquals(game.winner(), game.players().first().id());
    }

    @Test
    void requestMoveShouldDeclareWinAntiDiagonal() throws NoSuchFieldException, IllegalAccessException {
        Board board = game.board();
        PlayerRole[][] grid = new PlayerRole[board.size()][board.size()];
        grid[0][2] = game.currentRole();
        grid[1][1] = game.currentRole();
        grid[2][0] = null;

        Field gridField = Board.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        gridField.set(board, grid);

        Move winningMove = new Move(game.currentPlayer().id(), 2, 0);
        game.requestMove(winningMove);

        assertEquals(GameStatus.WON, game.status());
        assertEquals(game.winner(), game.players().first().id());
    }
}
