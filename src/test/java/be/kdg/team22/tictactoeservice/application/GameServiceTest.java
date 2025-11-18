package be.kdg.team22.tictactoeservice.application;

import be.kdg.team22.tictactoeservice.config.BoardSizeProperties;
import be.kdg.team22.tictactoeservice.domain.Game;
import be.kdg.team22.tictactoeservice.domain.GameId;
import be.kdg.team22.tictactoeservice.domain.NotFoundException;
import be.kdg.team22.tictactoeservice.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


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
    void shouldStartGameWithValidSize() {
        // Arrange
        Game expectedGame = new Game(5);
        doNothing().when(gameRepository).save(expectedGame);

        // Act
        Game game = gameService.startGame(5);

        // Asset
        assertNotNull(game);
        assertEquals(5, game.getBoard().getSize());
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void shouldThrowWhenToSmall() {
        assertThrows(IllegalArgumentException.class, () -> gameService.startGame(2));
    }

    @Test
    void shouldThrowWhenToBig() {
        assertThrows(IllegalArgumentException.class, () -> gameService.startGame(11));
    }

    @Test
    void shouldGetExistingGame() {
        Game storedGame = new Game(5);

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
}
