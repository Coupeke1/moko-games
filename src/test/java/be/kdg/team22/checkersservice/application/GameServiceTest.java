package be.kdg.team22.checkersservice.application;

import be.kdg.team22.checkersservice.application.events.GameEventPublisher;
import be.kdg.team22.checkersservice.domain.events.*;
import be.kdg.team22.checkersservice.domain.game.Game;
import be.kdg.team22.checkersservice.domain.game.GameId;
import be.kdg.team22.checkersservice.domain.game.GameStatus;
import be.kdg.team22.checkersservice.domain.move.KingMovementMode;
import be.kdg.team22.checkersservice.domain.move.Move;
import be.kdg.team22.checkersservice.domain.move.MoveResult;
import be.kdg.team22.checkersservice.domain.player.Player;
import be.kdg.team22.checkersservice.domain.player.PlayerId;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;
import be.kdg.team22.checkersservice.infrastructure.game.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class GameServiceTest {

    @Mock
    private GameRepository repository;

    @Mock
    private GameEventPublisher publisher;

    @InjectMocks
    private GameService service;

    private Player playerBlack;
    private Player playerWhite;
    private List<Player> players;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        playerBlack = new Player(PlayerId.create(), PlayerRole.BLACK, false);
        playerWhite = new Player(PlayerId.create(), PlayerRole.WHITE, false);
        players = List.of(playerBlack, playerWhite);
    }

    @Test
    void requestMoveShouldNotPublishAchievementWhenNoConditionsMet() {
        Game game = spy(Game.create(players.stream().map(Player::id).toList(), false, KingMovementMode.SINGLE));
        GameId id = game.id();
        PlayerId playerId = playerBlack.id();
        Move move = new Move(playerId, List.of(21, 17));

        when(repository.findById(id)).thenReturn(Optional.of(game));
        doReturn(GameStatus.RUNNING).when(game).status();

        service.requestMove(id, playerId, move);

        verify(repository).save(game);
        verify(publisher, never()).publishAchievement(any());
    }

    @Test
    void requestMoveShouldPublishAchievementWhenGameDraw() {
        Game game = spy(Game.create(players.stream().map(Player::id).toList(), false, KingMovementMode.SINGLE));
        GameId id = game.id();
        PlayerId playerId = playerBlack.id();
        Move move = new Move(playerId, List.of(21, 17));

        when(repository.findById(id)).thenReturn(Optional.of(game));
        doReturn(GameStatus.DRAW).when(game).status();

        service.requestMove(id, playerId, move);

        verify(repository).save(game);

        ArgumentCaptor<GameAchievementEvent> captor = ArgumentCaptor.forClass(GameAchievementEvent.class);
        verify(publisher, times(2)).publishAchievement(captor.capture());

        List<GameAchievementEvent> allValues = captor.getAllValues();

        GameAchievementEvent playerBlackDrawEvent = allValues.stream()
                .filter(event -> event.playerId().equals(playerBlack.id().value()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Draw event not found"));
        assertEquals(id.value(), playerBlackDrawEvent.gameId());
        assertEquals(playerBlack.id().value(), playerBlackDrawEvent.playerId());
        assertEquals(AchievementCode.CHECKERS_DRAW.name(), playerBlackDrawEvent.achievementCode());

        GameAchievementEvent playerWhiteDrawEvent = allValues.stream()
                .filter(event -> event.playerId().equals(playerWhite.id().value()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Draw event not found"));
        assertEquals(id.value(), playerWhiteDrawEvent.gameId());
        assertEquals(playerWhite.id().value(), playerWhiteDrawEvent.playerId());
        assertEquals(AchievementCode.CHECKERS_DRAW.name(), playerWhiteDrawEvent.achievementCode());
    }

    @Test
    void requestMoveShouldPublishAchievementsWhenBlackWon() {
        Game game = spy(Game.create(players.stream().map(Player::id).toList(), false, KingMovementMode.SINGLE));
        GameId id = game.id();
        PlayerId playerId = playerBlack.id();
        Move move = new Move(playerId, List.of(21, 17));

        when(repository.findById(id)).thenReturn(Optional.of(game));
        doReturn(PlayerRole.BLACK).when(game).currentRole();
        doReturn(playerBlack).when(game).currentPlayer();
        doReturn(GameStatus.BLACK_WIN).when(game).status();

        service.requestMove(id, playerId, move);

        verify(repository).save(game);

        ArgumentCaptor<GameAchievementEvent> captor = ArgumentCaptor.forClass(GameAchievementEvent.class);
        verify(publisher, times(2)).publishAchievement(captor.capture());

        List<GameAchievementEvent> allValues = captor.getAllValues();

        GameAchievementEvent winEvent = allValues.stream()
                .filter(event -> event.playerId().equals(playerBlack.id().value()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Win event not found"));
        assertEquals(id.value(), winEvent.gameId());
        assertEquals(playerBlack.id().value(), winEvent.playerId());
        assertEquals(AchievementCode.CHECKERS_WIN.name(), winEvent.achievementCode());

        GameAchievementEvent lossEvent = allValues.stream()
                .filter(event -> event.playerId().equals(playerWhite.id().value()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Loss event not found"));
        assertEquals(id.value(), lossEvent.gameId());
        assertEquals(playerWhite.id().value(), lossEvent.playerId());
        assertEquals(AchievementCode.CHECKERS_LOSS.name(), lossEvent.achievementCode());
    }

    @Test
    void requestMoveShouldPublishAchievementsWhenWhiteWon() {
        Game game = spy(Game.create(players.stream().map(Player::id).toList(), false, KingMovementMode.SINGLE));
        GameId id = game.id();
        PlayerId playerId = playerWhite.id();
        Move move = new Move(playerId, List.of(21, 17));

        when(repository.findById(id)).thenReturn(Optional.of(game));
        doReturn(PlayerRole.WHITE).when(game).currentRole();
        doReturn(playerWhite).when(game).currentPlayer();
        doReturn(GameStatus.WHITE_WIN).when(game).status();

        service.requestMove(id, playerId, move);

        verify(repository).save(game);

        ArgumentCaptor<GameAchievementEvent> captor = ArgumentCaptor.forClass(GameAchievementEvent.class);
        verify(publisher, times(2)).publishAchievement(captor.capture());

        List<GameAchievementEvent> allValues = captor.getAllValues();

        GameAchievementEvent winEvent = allValues.stream()
                .filter(event -> event.playerId().equals(playerWhite.id().value()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Win event not found"));
        assertEquals(id.value(), winEvent.gameId());
        assertEquals(playerWhite.id().value(), winEvent.playerId());
        assertEquals(AchievementCode.CHECKERS_WIN.name(), winEvent.achievementCode());

        GameAchievementEvent lossEvent = allValues.stream()
                .filter(event -> event.playerId().equals(playerBlack.id().value()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Loss event not found"));
        assertEquals(id.value(), lossEvent.gameId());
        assertEquals(playerBlack.id().value(), lossEvent.playerId());
        assertEquals(AchievementCode.CHECKERS_LOSS.name(), lossEvent.achievementCode());
    }

    @Test
    void requestMoveShouldPublishAchievementWhenKingPromotion() {
        Game game = spy(Game.create(players.stream().map(Player::id).toList(), false, KingMovementMode.SINGLE));
        GameId id = game.id();
        PlayerId playerId = playerBlack.id();
        Move move = new Move(playerId, List.of(21, 17));

        MoveResult promotionResult = new MoveResult(false, false, true, 1);

        when(repository.findById(id)).thenReturn(Optional.of(game));
        when(game.requestMove(move)).thenReturn(promotionResult);

        service.requestMove(id, playerId, move);

        verify(repository).save(game);

        ArgumentCaptor<GameAchievementEvent> captor = ArgumentCaptor.forClass(GameAchievementEvent.class);
        verify(publisher).publishAchievement(captor.capture());

        GameAchievementEvent event = captor.getValue();
        assertEquals(id.value(), event.gameId());
        assertEquals(
                playerBlack.id().value(),
                event.playerId()
        );
        assertEquals(AchievementCode.CHECKERS_PROMOTION.name(), event.achievementCode());
    }

    @Test
    void requestMoveShouldPublishAchievementWhenMakingMultiCapture() {
        Game game = spy(Game.create(players.stream().map(Player::id).toList(), false, KingMovementMode.SINGLE));
        GameId id = game.id();
        PlayerId playerId = playerBlack.id();
        Move move = new Move(playerId, List.of(21, 17));

        MoveResult promotionResult = new MoveResult(false, true, false, 0);

        when(repository.findById(id)).thenReturn(Optional.of(game));
        when(game.requestMove(move)).thenReturn(promotionResult);

        service.requestMove(id, playerId, move);

        verify(repository).save(game);

        ArgumentCaptor<GameAchievementEvent> captor = ArgumentCaptor.forClass(GameAchievementEvent.class);
        verify(publisher).publishAchievement(captor.capture());

        GameAchievementEvent event = captor.getValue();
        assertEquals(id.value(), event.gameId());
        assertEquals(
                playerBlack.id().value(),
                event.playerId()
        );
        assertEquals(AchievementCode.CHECKERS_MULTICAPTURE.name(), event.achievementCode());
    }

    @Test
    void requestMoveShouldPublishAchievementWhenThreeKingsPresent() {
        Game game = spy(Game.create(players.stream().map(Player::id).toList(), false, KingMovementMode.SINGLE));
        GameId id = game.id();
        PlayerId playerId = playerBlack.id();
        Move move = new Move(playerId, List.of(21, 17));

        MoveResult promotionResult = new MoveResult(false, false, false, 3);

        when(repository.findById(id)).thenReturn(Optional.of(game));
        when(game.requestMove(move)).thenReturn(promotionResult);

        service.requestMove(id, playerId, move);

        verify(repository).save(game);

        ArgumentCaptor<GameAchievementEvent> captor = ArgumentCaptor.forClass(GameAchievementEvent.class);
        verify(publisher).publishAchievement(captor.capture());

        GameAchievementEvent event = captor.getValue();
        assertEquals(id.value(), event.gameId());
        assertEquals(
                playerBlack.id().value(),
                event.playerId()
        );
        assertEquals(AchievementCode.CHECKERS_THREE_KINGS.name(), event.achievementCode());
    }
}
