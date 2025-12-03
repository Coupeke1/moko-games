package be.kdg.team22.checkersservice.domain.game;

import be.kdg.team22.checkersservice.domain.board.Move;
import be.kdg.team22.checkersservice.domain.game.exceptions.GameNotRunningException;
import be.kdg.team22.checkersservice.domain.game.exceptions.NotPlayersTurnException;
import be.kdg.team22.checkersservice.domain.game.exceptions.PlayerCountException;
import be.kdg.team22.checkersservice.domain.game.exceptions.UniquePlayersException;
import be.kdg.team22.checkersservice.domain.player.Player;
import be.kdg.team22.checkersservice.domain.player.PlayerId;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

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
                false
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
                Game.create(List.of(playerId), false));
    }

    @Test
    void createShouldThrowWhenTooManyPlayers() {
        List<PlayerId> playerIds = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerIds.add(PlayerId.create());
        }
        assertThrows(PlayerCountException.class, () ->
                Game.create(playerIds, false));
    }

    @Test
    void createShouldThrowWhenNonUniquePlayerIds() {
        UUID uuid = UUID.randomUUID();
        PlayerId player1 = new PlayerId(uuid);
        PlayerId player2 = new PlayerId(uuid);
        assertThrows(UniquePlayersException.class, () ->
                Game.create(List.of(player1, player2), false));
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
    void createWithAIPlayerShouldSetAiPlayerField() {
        Game gameWithAI = Game.create(
                List.of(playerBlackId, playerWhiteId),
                true
        );

        assertEquals(PlayerRole.WHITE, gameWithAI.aiPlayer());

        Player whitePlayer = gameWithAI.players().stream()
                .filter(p -> p.role() == PlayerRole.WHITE)
                .findFirst()
                .orElseThrow();
        assertTrue(whitePlayer.aiPlayer());

        Player blackPlayer = gameWithAI.players().stream()
                .filter(p -> p.role() == PlayerRole.BLACK)
                .findFirst()
                .orElseThrow();
        assertFalse(blackPlayer.aiPlayer());
    }

    @Test
    void createWithoutAIPlayerShouldHaveNullAiPlayer() {
        assertNull(game.aiPlayer());
        game.players().forEach(player -> assertFalse(player.aiPlayer()));
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

        Game specificGame = Game.create(List.of(blackId, whiteId), false);

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
        Move move = new Move(playerBlackId, 24, 20);

        game.requestMove(move);

        assertEquals(PlayerRole.WHITE, game.currentRole());
    }

    @Test
    void requestMoveShouldThrowWhenNotPlayersTurn() {
        Move move = new Move(playerWhiteId, 9, 13);

        assertThrows(NotPlayersTurnException.class, () ->
                game.requestMove(move)
        );
    }

    @Test
    void requestMoveShouldThrowWhenGameNotRunning() throws NoSuchFieldException, IllegalAccessException {
        Field statusField = Game.class.getDeclaredField("status");
        statusField.setAccessible(true);
        statusField.set(game, GameStatus.BLACK_WIN);
        Move move = new Move(playerBlackId, 24, 20);

        assertThrows(GameNotRunningException.class, () ->
                game.requestMove(move)
        );
    }
}