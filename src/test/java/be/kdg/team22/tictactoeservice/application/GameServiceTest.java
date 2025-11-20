package be.kdg.team22.tictactoeservice.application;

import be.kdg.team22.tictactoeservice.config.BoardSizeProperties;
import be.kdg.team22.tictactoeservice.domain.*;
import be.kdg.team22.tictactoeservice.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameServiceTest {
    @Mock
    private GameRepository gameRepository;

    @Mock
    private BoardSizeProperties boardConfig;

    @InjectMocks
    private GameService gameService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(boardConfig.getMinSize()).thenReturn(3);
        when(boardConfig.getMaxSize()).thenReturn(10);
    }

    @Test
    void shouldStartGameWithSize3x3() {
        // Arrange
        PlayerId playerXId = new PlayerId(UUID.randomUUID());
        PlayerId playerOId = new PlayerId(UUID.randomUUID());
        Game expectedGame = new Game(3, playerXId, playerOId);
        doNothing().when(gameRepository).save(expectedGame);

        // Act
        Game game = gameService.startGame(3, playerXId, playerOId);

        // Asset
        assertNotNull(game);
        assertEquals(3, game.getBoard().getSize());
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void shouldStartGameWithSize4x4() {
        // Arrange
        PlayerId playerXId = new PlayerId(UUID.randomUUID());
        PlayerId playerOId = new PlayerId(UUID.randomUUID());
        Game expectedGame = new Game(4, playerXId, playerOId);
        doNothing().when(gameRepository).save(expectedGame);

        // Act
        Game game = gameService.startGame(4,  playerXId, playerOId);

        // Asset
        assertNotNull(game);
        assertEquals(4, game.getBoard().getSize());
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void shouldStartGameWithSize5x5() {
        // Arrange
        PlayerId playerXId = new PlayerId(UUID.randomUUID());
        PlayerId playerOId = new PlayerId(UUID.randomUUID());
        Game expectedGame = new Game(5, playerXId, playerOId);
        doNothing().when(gameRepository).save(expectedGame);

        // Act
        Game game = gameService.startGame(5, playerXId, playerOId);

        // Asset
        assertNotNull(game);
        assertEquals(5, game.getBoard().getSize());
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void shouldThrowWhenToSmall() {
        assertThrows(IllegalArgumentException.class, () -> gameService.startGame(2, new PlayerId(UUID.randomUUID()), new PlayerId(UUID.randomUUID())));
    }

    @Test
    void shouldThrowWhenToBig() {
        assertThrows(IllegalArgumentException.class, () -> gameService.startGame(11, new PlayerId(UUID.randomUUID()), new PlayerId(UUID.randomUUID())));
    }

    @Test
    void shouldGetExistingGame() {
        Game storedGame = new Game(5,  new PlayerId(UUID.randomUUID()), new PlayerId(UUID.randomUUID()));

        when(gameRepository.findById(storedGame.getId())).thenReturn(Optional.of(storedGame));

        Game game = gameService.getGame(storedGame.getId());

        assertEquals(storedGame, game);
    }

    @Test
    void shouldThrowWhenGameNotFound() {
        GameId id = new GameId(UUID.randomUUID());

        when(gameRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gameService.getGame(id));
    }

    @Test
    void shouldResetExistingFinishedGame() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        Game storedGame = spy(new Game(3,  new PlayerId(UUID.randomUUID()), new PlayerId(UUID.randomUUID())));

        Field statusField = Game.class.getDeclaredField("status");
        statusField.setAccessible(true);
        statusField.set(storedGame, GameStatus.WON);

        GameId gameId = storedGame.getId();
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(storedGame));

        // Act
        Game result = gameService.resetGame(gameId);

        // Assert
        verify(storedGame).reset();
        verify(gameRepository).save(storedGame);
        assertEquals(storedGame, result);
    }

    @Test
    void shouldThrowWhenResettingInProgressGame() {
        // Arrange
        Game storedGame = spy(new Game(3, new PlayerId(UUID.randomUUID()), new PlayerId(UUID.randomUUID())));
        GameId gameId = storedGame.getId();
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(storedGame));

        // Assert
        assertThrows(IllegalStateException.class, () -> gameService.resetGame(gameId));
    }

    @Test
    void shouldThrowWhenResettingUnknownGame() {
        GameId id = GameId.create();
        when(gameRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gameService.resetGame(id));
    }
}
