package be.kdg.team22.checkersservice.application;

import be.kdg.team22.checkersservice.api.models.CreateGameModel;
import be.kdg.team22.checkersservice.application.events.GameEventPublisher;
import be.kdg.team22.checkersservice.domain.events.*;
import be.kdg.team22.checkersservice.domain.events.exceptions.PublishAchievementException;
import be.kdg.team22.checkersservice.domain.events.exceptions.RabbitNotReachableException;
import be.kdg.team22.checkersservice.domain.game.GameStatus;
import be.kdg.team22.checkersservice.domain.move.Move;
import be.kdg.team22.checkersservice.domain.game.Game;
import be.kdg.team22.checkersservice.domain.game.GameId;
import be.kdg.team22.checkersservice.domain.move.MoveResult;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;
import be.kdg.team22.checkersservice.domain.player.exceptions.PlayerIdentityMismatchException;
import be.kdg.team22.checkersservice.infrastructure.game.GameRepository;
import be.kdg.team22.checkersservice.domain.player.PlayerId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class GameService {
    private final GameRepository repository;
    private final GameEventPublisher publisher;

    private final Logger logger;

    public GameService(final GameRepository repository, final GameEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
        logger = LoggerFactory.getLogger(GameService.class);
    }

    public Game create(final CreateGameModel model, final boolean aiPlayer) {
        List<PlayerId> players = model.players().stream().map(PlayerId::new).toList();
        Game game = Game.create(players, aiPlayer, model.settings().kingMovementMode());
        repository.save(game);
        return game;
    }

    public Game getById(final GameId id) {
        return repository.findById(id).orElseThrow(id::notFound);
    }

    public Game requestMove(final GameId gameId, final PlayerId playerId, final Move move) {
        if (!playerId.equals(move.playerId())) throw new PlayerIdentityMismatchException();

        Game game = getById(gameId);
        MoveResult result = game.requestMove(move);
        repository.save(game);

        try {
            switch (game.status()) {
                case GameStatus.DRAW -> publisher.publishGameDraw(
                        new GameDrawEvent(
                                game.id().value(),
                                game.players().stream()
                                        .map(p -> p.id().value())
                                        .toList(),
                                Instant.now()
                        )
                );
                case GameStatus.BLACK_WIN -> {
                    publisher.publishGameWon(
                            new GameWonEvent(
                                    game.id().value(),
                                    game.playerWithRole(PlayerRole.BLACK).id().value(),
                                    Instant.now()
                            )
                    );
                    publisher.publishGameLost(
                            new GameLostEvent(game.id().value(),
                                    game.playerWithRole(PlayerRole.WHITE).id().value(),
                                    Instant.now())
                    );
                }
                case GameStatus.WHITE_WIN -> {
                    publisher.publishGameWon(
                            new GameWonEvent(
                                    game.id().value(),
                                    game.playerWithRole(PlayerRole.WHITE).id().value(),
                                    Instant.now()
                            )
                    );
                    publisher.publishGameLost(
                            new GameLostEvent(game.id().value(),
                                    game.playerWithRole(PlayerRole.BLACK).id().value(),
                                    Instant.now())
                    );
                }
            }

            if (result.promotion()) {
                publisher.publishKingPromotionEvent(
                        new KingPromotionEvent(
                                game.id().value(),
                                move.playerId().value(),
                                Instant.now()
                        )
                );
            }

            if (result.multiCapture()) {
                publisher.publishMultiCaptureEvent(
                        new MultiCaptureEvent(
                                game.id().value(),
                                move.playerId().value(),
                                Instant.now()
                        )
                );
            }

            if (result.kingCount() >= 3) {
                publisher.publishThreeKingsEvent(
                        new ThreeKingsEvent(
                                game.id().value(),
                                move.playerId().value(),
                                Instant.now()
                        )
                );
            }
        } catch (PublishAchievementException | RabbitNotReachableException exception) {
            logger.error(exception.getMessage());
        }

        return game;
    }

    public Game reset(final GameId id) {
        Game game = getById(id);
        game.reset();
        repository.save(game);
        return game;
    }
}