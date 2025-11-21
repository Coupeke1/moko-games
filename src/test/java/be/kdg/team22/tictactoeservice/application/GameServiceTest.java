package be.kdg.team22.tictactoeservice.application;

import be.kdg.team22.tictactoeservice.config.BoardSizeProperties;
import be.kdg.team22.tictactoeservice.domain.game.Game;
import be.kdg.team22.tictactoeservice.domain.game.GameId;
import be.kdg.team22.tictactoeservice.domain.game.GameStatus;
import be.kdg.team22.tictactoeservice.domain.game.exceptions.BoardSizeException;
import be.kdg.team22.tictactoeservice.domain.game.exceptions.GameResetException;
import be.kdg.team22.tictactoeservice.domain.game.exceptions.NotFoundException;
import be.kdg.team22.tictactoeservice.domain.player.Player;
import be.kdg.team22.tictactoeservice.domain.player.PlayerId;
import be.kdg.team22.tictactoeservice.domain.player.PlayerRole;
import be.kdg.team22.tictactoeservice.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameServiceTest {
    @Mock
    private GameRepository repository;

    @Mock
    private BoardSizeProperties config;

    @InjectMocks
    private GameService service;

    private Player playerX;
    private Player playerO;
    private List<Player> players;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(config.minSize()).thenReturn(3);
        when(config.maxSize()).thenReturn(10);

        playerX = new Player(PlayerId.create(), PlayerRole.X);
        playerO = new Player(PlayerId.create(), PlayerRole.O);
        players = List.of(playerX, playerO);
    }

    @Test
    void shouldStartGameWithSize3x3() {
        // Arrange
        Game expectedGame = Game.create(config.minSize(), config.maxSize(), 3, players);
        doNothing().when(repository).save(expectedGame);

        // Act
        Game game = service.startGame(3, List.of(playerX, playerO));

        // Asset
        assertNotNull(game);
        assertEquals(3, game.board().size());
        verify(repository).save(any(Game.class));
    }

    @Test
    void shouldStartGameWithSize4x4() {
        // Arrange
        Game expectedGame = Game.create(config.minSize(), config.maxSize(), 4, players);
        doNothing().when(repository).save(expectedGame);
        // Act
        Game game = service.startGame(4, players);

        // Asset
        assertNotNull(game);
        assertEquals(4, game.board().size());
        verify(repository).save(any(Game.class));
    }

    @Test
    void shouldStartGameWithSize5x5() {
        // Arrange
        Game expectedGame = Game.create(config.minSize(), config.maxSize(), 5, players);
        doNothing().when(repository).save(expectedGame);

        // Act
        Game game = service.startGame(5, players);

        // Asset
        assertNotNull(game);
        assertEquals(5, game.board().size());
        verify(repository).save(any(Game.class));
    }

    @Test
    void shouldThrowWhenTooSmall() {
        assertThrows(BoardSizeException.class, () -> service.startGame(2, players));
    }

    @Test
    void shouldThrowWhenTooBig() {
        assertThrows(BoardSizeException.class, () -> service.startGame(11, players));
    }

    @Test
    void shouldGetExistingGame() {
        Game storedGame = Game.create(config.minSize(), config.maxSize(), 5, players);

        when(repository.findById(storedGame.id())).thenReturn(Optional.of(storedGame));

        Game game = service.getGame(storedGame.id());

        assertEquals(storedGame, game);
    }

    @Test
    void shouldThrowWhenGameNotFound() {
        GameId id = new GameId(UUID.randomUUID());

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getGame(id));
    }

    @Test
    void shouldResetExistingFinishedGame() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        Game storedGame = spy(Game.create(config.minSize(), config.maxSize(), 3, players));

        Field statusField = Game.class.getDeclaredField("status");
        statusField.setAccessible(true);
        statusField.set(storedGame, GameStatus.WON);

        GameId gameId = storedGame.id();
        when(repository.findById(gameId)).thenReturn(Optional.of(storedGame));

        // Act
        Game result = service.resetGame(gameId);

        // Assert
        verify(storedGame).reset();
        verify(repository).save(storedGame);
        assertEquals(storedGame, result);
    }

    @Test
    void shouldThrowWhenResettingInProgressGame() {
        // Arrange
        Game storedGame = spy(Game.create(config.minSize(), config.maxSize(), 3, players));
        GameId gameId = storedGame.id();
        when(repository.findById(gameId)).thenReturn(Optional.of(storedGame));

        // Assert
        assertThrows(GameResetException.class, () -> service.resetGame(gameId));
    }

    @Test
    void shouldThrowWhenResettingUnknownGame() {
        GameId id = GameId.create();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.resetGame(id));
    }
}
