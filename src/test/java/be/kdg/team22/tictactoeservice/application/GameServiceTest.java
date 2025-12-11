package be.kdg.team22.tictactoeservice.application;

import be.kdg.team22.tictactoeservice.api.models.CreateGameModel;
import be.kdg.team22.tictactoeservice.api.models.GameSettingsModel;
import be.kdg.team22.tictactoeservice.application.events.GameEventPublisher;
import be.kdg.team22.tictactoeservice.config.BoardSizeProperties;
import be.kdg.team22.tictactoeservice.domain.events.AchievementCode;
import be.kdg.team22.tictactoeservice.domain.events.GameAchievementEvent;
import be.kdg.team22.tictactoeservice.domain.events.GameEndedEvent;
import be.kdg.team22.tictactoeservice.domain.game.Game;
import be.kdg.team22.tictactoeservice.domain.game.GameId;
import be.kdg.team22.tictactoeservice.domain.game.GameStatus;
import be.kdg.team22.tictactoeservice.domain.game.Move;
import be.kdg.team22.tictactoeservice.domain.game.exceptions.BoardSizeException;
import be.kdg.team22.tictactoeservice.domain.player.Player;
import be.kdg.team22.tictactoeservice.domain.player.PlayerId;
import be.kdg.team22.tictactoeservice.domain.player.PlayerRole;
import be.kdg.team22.tictactoeservice.infrastructure.game.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameServiceTest {

    @Mock
    private GameRepository repository;

    @Mock
    private BoardSizeProperties config;

    @Mock
    private GameEventPublisher publisher;

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

        playerX = new Player(PlayerId.create(), PlayerRole.X, false);
        playerO = new Player(PlayerId.create(), PlayerRole.O, false);
        players = List.of(playerX, playerO);
    }

    @Test
    void shouldStartGameWithSize3x3() {
        doNothing().when(repository).save(any());

        CreateGameModel model = new CreateGameModel(
                players.stream().map(p -> p.id().value()).toList(),
                new GameSettingsModel(3));

        Game game = service.startGame(model, playerX.id(), false);

        assertNotNull(game);
        assertEquals(3, game.board().size());
        verify(repository).save(any(Game.class));
    }

    @Test
    void shouldStartGameWithSize4x4() {
        doNothing().when(repository).save(any());

        CreateGameModel model = new CreateGameModel(
                players.stream().map(p -> p.id().value()).toList(),
                new GameSettingsModel(4));

        Game game = service.startGame(model, playerX.id(), false);

        assertEquals(4, game.board().size());
        verify(repository).save(any(Game.class));
    }

    @Test
    void shouldThrowWhenTooSmall() {
        CreateGameModel model = new CreateGameModel(
                players.stream().map(p -> p.id().value()).toList(),
                new GameSettingsModel(2));

        assertThrows(BoardSizeException.class, () -> service.startGame(model, playerX.id(), false));
    }

    @Test
    void shouldGetExistingGame() {
        Game stored = Game.create(3, 10, 3, players.stream().map(Player::id).toList(), false);
        when(repository.findById(stored.id())).thenReturn(Optional.of(stored));

        Game result = service.getGame(stored.id(), playerX.id());

        assertEquals(stored, result);
    }

    @Test
    void requestMove_noWinner_noDraw_noEventPublished() {
        Game game = spy(Game.create(3, 10, 3, players.stream().map(Player::id).toList(), false));
        GameId id = game.id();
        PlayerId playerId = playerX.id();

        when(repository.findById(id)).thenReturn(Optional.of(game));

        Move move = new Move(playerId, 0, 0);

        doReturn(GameStatus.IN_PROGRESS).when(game).status();

        service.requestMove(id, playerId, move);

        verify(repository).save(game);
        verify(publisher, never()).publishAchievement(any());
        verify(publisher, never()).publishGameEnded(any());
    }

    @Test
    void requestMove_whenWin_publishesGameWon() {
        Game game = spy(Game.create(3, 10, 3, players.stream().map(Player::id).toList(), false));
        GameId id = game.id();
        PlayerId playerId = playerX.id();

        when(repository.findById(id)).thenReturn(Optional.of(game));

        Move move = new Move(playerId, 0, 0);

        doReturn(GameStatus.WON).when(game).status();
        doReturn(playerX.id()).when(game).winner();

        service.requestMove(id, playerId, move);

        verify(repository).save(game);

        ArgumentCaptor<GameAchievementEvent> captor = ArgumentCaptor.forClass(GameAchievementEvent.class);
        verify(publisher).publishAchievement(captor.capture());

        GameAchievementEvent event = captor.getValue();
        assertEquals(id.value(), event.gameId());
        assertEquals(playerId.value(), event.playerId());
        assertEquals(AchievementCode.TICTACTOE_WIN.name(), event.achievementCode());
    }

    @Test
    void requestMove_whenDraw_publishesGameDraw() {
        Game game = spy(Game.create(3, 10, 3, players.stream().map(Player::id).toList(), false));
        GameId id = game.id();
        PlayerId playerId = playerX.id();

        when(repository.findById(id)).thenReturn(Optional.of(game));

        Move move = new Move(playerId, 0, 0);

        doReturn(GameStatus.TIE).when(game).status();
        TreeSet<Player> sorted = new TreeSet<>(Comparator.comparing(p -> p.role().order()));
        sorted.addAll(players);

        doReturn(sorted).when(game).players();
        service.requestMove(id, playerId, move);

        verify(repository).save(game);

        ArgumentCaptor<GameAchievementEvent> captor = ArgumentCaptor.forClass(GameAchievementEvent.class);
        verify(publisher, times(2)).publishAchievement(captor.capture());

        List<GameAchievementEvent> allValues = captor.getAllValues();

        GameAchievementEvent playerXDrawEvent = allValues.stream()
                .filter(event -> event.playerId().equals(playerX.id().value()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Draw event not found"));
        assertEquals(id.value(), playerXDrawEvent.gameId());
        assertEquals(playerX.id().value(), playerXDrawEvent.playerId());
        assertEquals(AchievementCode.TICTACTOE_DRAW.name(), playerXDrawEvent.achievementCode());

        GameAchievementEvent playerODrawEvent = allValues.stream()
                .filter(event -> event.playerId().equals(playerO.id().value()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Draw event not found"));
        assertEquals(id.value(), playerODrawEvent.gameId());
        assertEquals(playerO.id().value(), playerODrawEvent.playerId());
        assertEquals(AchievementCode.TICTACTOE_DRAW.name(), playerODrawEvent.achievementCode());

        ArgumentCaptor<GameEndedEvent> endedCaptor = ArgumentCaptor.forClass(GameEndedEvent.class);
        verify(publisher).publishGameEnded(endedCaptor.capture());
        assertEquals(id.value(), endedCaptor.getValue().instanceId());
    }

    @Test
    void requestMove_whenBecomesWin_publishesOnce() {
        Game game = spy(Game.create(3, 10, 3, players.stream().map(Player::id).toList(), false));
        GameId id = game.id();
        PlayerId playerId = playerX.id();

        when(repository.findById(id)).thenReturn(Optional.of(game));

        Move move = new Move(playerId, 0, 0);

        doNothing().when(game).requestMove(any());

        doReturn(GameStatus.WON).when(game).status();
        doReturn(playerX.id()).when(game).winner();

        service.requestMove(id, playerId, move);

        verify(publisher, times(1)).publishAchievement(any(GameAchievementEvent.class));
        verify(publisher, times(1)).publishGameEnded(any(GameEndedEvent.class));
    }
}