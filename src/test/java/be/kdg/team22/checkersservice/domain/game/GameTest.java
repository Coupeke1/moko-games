package be.kdg.team22.checkersservice.domain.game;

import be.kdg.team22.checkersservice.domain.board.Board;
import be.kdg.team22.checkersservice.domain.board.Piece;
import be.kdg.team22.checkersservice.domain.game.exceptions.*;
import be.kdg.team22.checkersservice.domain.move.KingMovementMode;
import be.kdg.team22.checkersservice.domain.move.Move;
import be.kdg.team22.checkersservice.domain.move.MoveResult;
import be.kdg.team22.checkersservice.domain.move.exceptions.*;
import be.kdg.team22.checkersservice.domain.player.Player;
import be.kdg.team22.checkersservice.domain.player.PlayerId;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    private Game game;
    private PlayerId playerBlackId;
    private PlayerId playerWhiteId;

    @BeforeEach
    public void setup() {
        playerBlackId = PlayerId.create();
        playerWhiteId = PlayerId.create();
        game = Game.create(
                List.of(playerBlackId, playerWhiteId),
                false, KingMovementMode.FLYING
        );
    }

    @Test
    void shouldHaveInitialStateCorrect() {
        assertNotNull(game.id());
        assertEquals(GameStatus.RUNNING, game.status());
        assertEquals(PlayerRole.BLACK, game.currentRole());
        assertNotNull(game.board());
        assertNotNull(game.players());
        assertEquals(2, game.players().size());
    }

    @Test
    void createShouldThrowWhenTooFewPlayers() {
        PlayerId playerId = PlayerId.create();
        assertThrows(PlayerCountException.class, () ->
                Game.create(List.of(playerId), false, KingMovementMode.FLYING));
    }

    @Test
    void createShouldThrowWhenTooManyPlayers() {
        List<PlayerId> playerIds = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerIds.add(PlayerId.create());
        }
        assertThrows(PlayerCountException.class, () ->
                Game.create(playerIds, false, KingMovementMode.FLYING));
    }

    @Test
    void createShouldThrowWhenNonUniquePlayerIds() {
        UUID uuid = UUID.randomUUID();
        PlayerId player1 = new PlayerId(uuid);
        PlayerId player2 = new PlayerId(uuid);
        assertThrows(UniquePlayersException.class, () ->
                Game.create(List.of(player1, player2), false, KingMovementMode.FLYING));
    }

    @Test
    void initialRoleShouldBeBlackPlayerRole() {
        assertEquals(PlayerRole.BLACK, game.currentRole());

        Player firstPlayer = game.players().first();
        assertEquals(PlayerRole.BLACK, firstPlayer.role());
    }

    @Test
    void playersShouldBeSortedByRoleOrder() {
        TreeSet<Player> players = game.players();
        List<Player> playerList = new ArrayList<>(players);

        assertEquals(PlayerRole.BLACK, playerList.get(0).role());
        assertEquals(PlayerRole.WHITE, playerList.get(1).role());
    }

    @Test
    void createWithBotPlayerShouldSetBotPlayerField() {
        Game gameWithBot = Game.create(
                List.of(playerBlackId, playerWhiteId),
                true, KingMovementMode.FLYING
        );

        assertEquals(PlayerRole.WHITE, gameWithBot.botPlayer());

        Player whitePlayer = gameWithBot.players().stream()
                .filter(p -> p.role() == PlayerRole.WHITE)
                .findFirst()
                .orElseThrow();
        assertTrue(whitePlayer.botPlayer());

        Player blackPlayer = gameWithBot.players().stream()
                .filter(p -> p.role() == PlayerRole.BLACK)
                .findFirst()
                .orElseThrow();
        assertFalse(blackPlayer.botPlayer());
    }

    @Test
    void createWithoutBotPlayerShouldHaveNullBotPlayer() {
        assertNull(game.botPlayer());
        game.players().forEach(player -> assertFalse(player.botPlayer()));
    }

    @Test
    void gameIdShouldBeGenerated() {
        assertNotNull(game.id());
        assertNotNull(game.id().value());
    }

    @Test
    void gameStatusShouldBeRunningInitially() {
        assertEquals(GameStatus.RUNNING, game.status());
    }

    @Test
    void currentRoleShouldBeBlackInitially() {
        assertEquals(PlayerRole.BLACK, game.currentRole());
    }

    @Test
    void shouldCreateGameWithSpecificPlayerIds() {
        UUID blackUuid = UUID.randomUUID();
        UUID whiteUuid = UUID.randomUUID();
        PlayerId blackId = new PlayerId(blackUuid);
        PlayerId whiteId = new PlayerId(whiteUuid);

        Game specificGame = Game.create(List.of(blackId, whiteId),
                false, KingMovementMode.FLYING);

        TreeSet<Player> players = specificGame.players();
        assertEquals(2, players.size());

        Player blackPlayer = players.first();
        Player whitePlayer = players.last();

        assertEquals(blackUuid, blackPlayer.id().value());
        assertEquals(whiteUuid, whitePlayer.id().value());
        assertEquals(PlayerRole.BLACK, blackPlayer.role());
        assertEquals(PlayerRole.WHITE, whitePlayer.role());
    }

    @Test
    void requestMoveShouldChangeCurrentRole() {
        Move move = new Move(playerBlackId, List.of(24, 20));

        game.requestMove(move);

        assertEquals(PlayerRole.WHITE, game.currentRole());
    }

    @Test
    void requestMoveShouldRemovePieceWhenFlyingKingCapturesFromDistance() throws NoSuchFieldException, IllegalAccessException {
        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        boardField.set(game, createKingBoard());
        Move move = new Move(playerBlackId, List.of(23, 12));

        game.requestMove(move);
        assertEquals(Optional.empty(), game.board().pieceAt(16));
    }

    @Test
    void requestMoveShouldMovePieceWhenFlyingKingMoves() throws NoSuchFieldException, IllegalAccessException {
        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        boardField.set(game, createKingBoard());
        Move move = new Move(playerBlackId, List.of(18, 29));

        game.requestMove(move);
        assertEquals(Optional.empty(), game.board().pieceAt(18));
        assertNotEquals(Optional.empty(), game.board().pieceAt(29));
    }

    @Test
    void requestMoveShouldThrowWhenNotPlayersTurn() {
        Move move = new Move(playerWhiteId, List.of(9, 13));

        assertThrows(NotPlayersTurnException.class, () ->
                game.requestMove(move)
        );
    }

    @Test
    void requestMoveShouldThrowWhenGameNotRunning() throws NoSuchFieldException, IllegalAccessException {
        Field statusField = Game.class.getDeclaredField("status");
        statusField.setAccessible(true);
        statusField.set(game, GameStatus.BLACK_WIN);
        Move move = new Move(playerBlackId, List.of(24, 20));

        assertThrows(GameNotRunningException.class, () ->
                game.requestMove(move)
        );
    }

    @Test
    void requestMoveShouldThrowWhenOwnPieceInTheWay() throws NoSuchFieldException, IllegalAccessException {
        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        boardField.set(game, createKingBoard());
        Move move = new Move(playerBlackId, List.of(18, 27));

        assertThrows(OwnPieceInTheWayException.class, () ->
                game.requestMove(move)
        );
    }

    @Test
    void requestMoveShouldThrowWhenKingDoubleMoveWithOnlySingleAllowed() throws NoSuchFieldException, IllegalAccessException {
        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        boardField.set(game, createKingBoard());
        Field movementModeField = Game.class.getDeclaredField("kingMovementMode");
        movementModeField.setAccessible(true);
        movementModeField.set(game, KingMovementMode.SINGLE);
        Move move = new Move(playerBlackId, List.of(18, 25));

        assertThrows(TooManyTilesException.class, () ->
                game.requestMove(move)
        );
    }

    @Test
    void requestMoveShouldThrowWhenTwoStepKingKeepsMovingAfterCapturingPiece() throws NoSuchFieldException, IllegalAccessException {
        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        boardField.set(game, createKingBoard());
        Field movementModeField = Game.class.getDeclaredField("kingMovementMode");
        movementModeField.setAccessible(true);
        movementModeField.set(game, KingMovementMode.DOUBLE);
        Move move = new Move(playerBlackId, List.of(18, 4));

        assertThrows(CapturedPieceNotOnLastTileException.class, () ->
                game.requestMove(move)
        );
    }

    @Test
    void requestMoveShouldThrowWhenFlyingKingKeepsMovingAfterCapturingPiece() throws NoSuchFieldException, IllegalAccessException {
        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        boardField.set(game, createKingBoard());
        Move move = new Move(playerBlackId, List.of(18, 4));

        assertThrows(CapturedPieceNotOnLastTileException.class, () ->
                game.requestMove(move)
        );
    }

    @Test
    void requestMoveShouldThrowWhenFlyingKingMovesToSameSpace() throws NoSuchFieldException, IllegalAccessException {
        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        boardField.set(game, createKingBoard());
        Move move = new Move(playerBlackId, List.of(18, 18));

        assertThrows(TargetCellNotEmptyException.class, () ->
                game.requestMove(move)
        );
    }

    @Test
    void requestMoveShouldAllowMultiCapture() throws NoSuchFieldException, IllegalAccessException {
        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        boardField.set(game, createMultiCaptureBoard());
        Move move = new Move(game.players().first().id(), List.of(22, 13, 6));

        MoveResult result = game.requestMove(move);
        assert (result.multiCapture());
        assert (result.capture());
        assert (game.board().pieceAt(17).isEmpty());
        assert (game.board().pieceAt(9).isEmpty());
    }

    @Test
    void requestMoveShouldThrowWhenSteppingAfterCapture() throws NoSuchFieldException, IllegalAccessException {
        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        boardField.set(game, createMultiCaptureBoard());
        Move move = new Move(game.players().first().id(), List.of(22, 13, 6, 2));

        assertThrows(TooManyMovesException.class, () ->
                game.requestMove(move)
        );
        assert (game.board().pieceAt(22).isPresent());
        assert (game.board().pieceAt(17).isPresent());
        assert (game.board().pieceAt(9).isPresent());
    }

    @Test
    void requestMoveShouldThrowWhenTryingToStepMultipleTimes() throws NoSuchFieldException, IllegalAccessException {
        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        boardField.set(game, createMultiCaptureBoard());
        Move move = new Move(game.players().first().id(), List.of(22, 18, 15));

        assertThrows(TooManyMovesException.class, () ->
                game.requestMove(move)
        );
        assert (game.board().pieceAt(22).isPresent());
    }

    private Board createMultiCaptureBoard() {
        Board testBoard = Board.create(8);
        clearBoard(testBoard);

        testBoard.grid().put(22, new Piece(PlayerRole.BLACK, false));
        testBoard.grid().put(17, new Piece(PlayerRole.WHITE, false));
        testBoard.grid().put(9, new Piece(PlayerRole.WHITE, false));

        return testBoard;
    }

    private Board createKingBoard() {
        Board testBoard = Board.create(8);
        clearBoard(testBoard);


        testBoard.grid().put(18, new Piece(PlayerRole.BLACK, true));
        testBoard.grid().put(23, new Piece(PlayerRole.BLACK, true));
        testBoard.grid().put(15, new Piece(PlayerRole.WHITE, true));
        testBoard.grid().put(16, new Piece(PlayerRole.WHITE, true));

        return testBoard;
    }

    private void clearBoard(Board board) {
        int totalCells = (board.size() * board.size()) / 2;
        for (int i = 1; i <= totalCells; i++) {
            board.grid().put(i, null);
        }
    }
}