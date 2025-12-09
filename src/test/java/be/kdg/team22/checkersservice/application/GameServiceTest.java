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
        verify(publisher, never()).publishGameDraw(any());
        verify(publisher, never()).publishGameLost(any());
        verify(publisher, never()).publishGameWon(any());
        verify(publisher, never()).publishKingPromotionEvent(any());
        verify(publisher, never()).publishMultiCaptureEvent(any());
        verify(publisher, never()).publishThreeKingsEvent(any());
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

        ArgumentCaptor<GameDrawEvent> captor = ArgumentCaptor.forClass(GameDrawEvent.class);
        verify(publisher).publishGameDraw(captor.capture());

        GameDrawEvent event = captor.getValue();
        assertEquals(id.value(), event.gameId());
        assertEquals(
                List.of(playerBlack.id().value(), playerWhite.id().value()),
                event.players()
        );
    }

    @Test
    void requestMoveShouldPublishAchievementsWhenBlackWon() {
        Game game = spy(Game.create(players.stream().map(Player::id).toList(), false, KingMovementMode.SINGLE));
        GameId id = game.id();
        PlayerId playerId = playerBlack.id();
        Move move = new Move(playerId, List.of(21, 17));

        when(repository.findById(id)).thenReturn(Optional.of(game));
        doReturn(GameStatus.BLACK_WIN).when(game).status();

        service.requestMove(id, playerId, move);

        verify(repository).save(game);

        ArgumentCaptor<GameWonEvent> winCaptor = ArgumentCaptor.forClass(GameWonEvent.class);
        verify(publisher).publishGameWon(winCaptor.capture());

        GameWonEvent winEvent = winCaptor.getValue();
        assertEquals(id.value(), winEvent.gameId());
        assertEquals(
                playerBlack.id().value(),
                winEvent.winnerId()
        );

        ArgumentCaptor<GameLostEvent> loseCaptor = ArgumentCaptor.forClass(GameLostEvent.class);
        verify(publisher).publishGameLost(loseCaptor.capture());

        GameLostEvent event = loseCaptor.getValue();
        assertEquals(id.value(), event.gameId());
        assertEquals(
                playerWhite.id().value(),
                event.loserId()
        );
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

        ArgumentCaptor<KingPromotionEvent> captor = ArgumentCaptor.forClass(KingPromotionEvent.class);
        verify(publisher).publishKingPromotionEvent(captor.capture());

        KingPromotionEvent event = captor.getValue();
        assertEquals(id.value(), event.gameId());
        assertEquals(
                playerBlack.id().value(),
                event.playerId()
        );
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

        ArgumentCaptor<MultiCaptureEvent> captor = ArgumentCaptor.forClass(MultiCaptureEvent.class);
        verify(publisher).publishMultiCaptureEvent(captor.capture());

        MultiCaptureEvent event = captor.getValue();
        assertEquals(id.value(), event.gameId());
        assertEquals(
                playerBlack.id().value(),
                event.playerId()
        );
    }

    @Test
    void requestMoveShouldPublishAchievementWhenThreeKingsPresent() {
        Game game = spy(Game.create(players.stream().map(Player::id).toList(), false, KingMovementMode.SINGLE));
        GameId id = game.id();
        PlayerId playerId = playerBlack.id();
        Move move = new Move(playerId, List.of(21, 17));

        MoveResult promotionResult = new MoveResult(false, true, false, 3);

        when(repository.findById(id)).thenReturn(Optional.of(game));
        when(game.requestMove(move)).thenReturn(promotionResult);

        service.requestMove(id, playerId, move);

        verify(repository).save(game);

        ArgumentCaptor<ThreeKingsEvent> captor = ArgumentCaptor.forClass(ThreeKingsEvent.class);
        verify(publisher).publishThreeKingsEvent(captor.capture());

        ThreeKingsEvent event = captor.getValue();
        assertEquals(id.value(), event.gameId());
        assertEquals(
                playerBlack.id().value(),
                event.playerId()
        );
    }
}
