package be.kdg.team22.tictactoeservice.application;

import be.kdg.team22.tictactoeservice.api.models.CreateGameModel;
import be.kdg.team22.tictactoeservice.application.events.GameEventPublisher;
import be.kdg.team22.tictactoeservice.config.BoardSizeProperties;
import be.kdg.team22.tictactoeservice.config.GameInfoProperties;
import be.kdg.team22.tictactoeservice.domain.events.AchievementCode;
import be.kdg.team22.tictactoeservice.domain.events.GameAchievementEvent;
import be.kdg.team22.tictactoeservice.domain.events.GameEndedEvent;
import be.kdg.team22.tictactoeservice.domain.events.exceptions.PublishAchievementException;
import be.kdg.team22.tictactoeservice.domain.events.exceptions.RabbitNotReachableException;
import be.kdg.team22.tictactoeservice.domain.game.Game;
import be.kdg.team22.tictactoeservice.domain.game.GameId;
import be.kdg.team22.tictactoeservice.domain.game.GameStatus;
import be.kdg.team22.tictactoeservice.domain.game.Move;
import be.kdg.team22.tictactoeservice.domain.game.exceptions.NotPlayersTurnException;
import be.kdg.team22.tictactoeservice.domain.player.PlayerId;
import be.kdg.team22.tictactoeservice.domain.player.exceptions.PlayerIdentityMismatchException;
import be.kdg.team22.tictactoeservice.domain.player.exceptions.PlayerNotInThisGameException;
import be.kdg.team22.tictactoeservice.events.BotMoveRequestedEvent;
import be.kdg.team22.tictactoeservice.infrastructure.game.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class GameService {
    private final GameRepository repository;
    private final BoardSizeProperties config;
    private final GameEventPublisher publisher;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final GameInfoProperties gameInfo;

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    public GameService(GameRepository repository, BoardSizeProperties config, GameEventPublisher publisher, ApplicationEventPublisher applicationEventPublisher, GameInfoProperties gameInfo) {
        this.repository = repository;
        this.config = config;
        this.publisher = publisher;
        this.applicationEventPublisher = applicationEventPublisher;
        this.gameInfo = gameInfo;
    }

    public Game create(final PlayerId playerId, final CreateGameModel model) {
        List<PlayerId> players = model.players().stream().map(PlayerId::new).toList();
        if (players.stream().noneMatch(p -> p.equals(playerId)))
            throw new PlayerNotInThisGameException();
        Game game = Game.create(config.minSize(), config.maxSize(), model.hasBot() ? 3 : model.settings().boardSize(), players, model.hasBot());
        repository.save(game);
        return game;
    }

    public Game getById(final GameId id) {
        return repository.findById(id).orElseThrow(id::notFound);
    }

    public Game requestState(final GameId id, final PlayerId playerId) {
        Game game = getById(id);
        if (game.players().stream().noneMatch(p -> p.id().equals(playerId)))
            throw new PlayerNotInThisGameException();
        return game;
    }

    public Game requestMove(final GameId id, final PlayerId playerId, final Move move) {
        if (!playerId.equals(move.playerId())) throw new PlayerIdentityMismatchException();

        Game game = getById(id);
        game.requestMove(move);
        repository.save(game);

        if (game.currentPlayer().botPlayer()) {
            boolean expectResponse = game.status() == GameStatus.IN_PROGRESS;
            applicationEventPublisher.publishEvent(BotMoveRequestedEvent.from(game, expectResponse));
        }

        checkForEvents(game);

        return game;
    }

    public Game requestBotMove(final GameId gameId, final PlayerId playerId) {
        Game game = getById(gameId);
        if (game.players().stream().noneMatch(p -> p.id().equals(playerId)))
            throw new PlayerNotInThisGameException();

        if (!game.currentPlayer().botPlayer())
            throw new NotPlayersTurnException(
                    game.players().stream()
                            .filter(p -> p.role()
                                    .equals(game.botPlayer()))
                            .findFirst().orElseThrow()
                            .id().value());

        boolean expectResponse = game.status() == GameStatus.IN_PROGRESS;
        applicationEventPublisher.publishEvent(BotMoveRequestedEvent.from(game, expectResponse));

        checkForEvents(game);

        return game;
    }

    private void checkForEvents(final Game game) {
        String gameName = gameInfo.name();
        try {
            if (game.status() == GameStatus.TIE) {
                game.players().forEach(player ->
                        publisher.publishAchievement(
                                new GameAchievementEvent(
                                        AchievementCode.DRAW.name(),
                                        gameName,
                                        game.id().value(),
                                        player.id().value(),
                                        Instant.now()
                                )
                        ));
            }

            if (game.status() == GameStatus.WON) {
                publisher.publishAchievement(
                        new GameAchievementEvent(
                                AchievementCode.WIN.name(),
                                gameName,
                                game.id().value(),
                                game.winner().value(),
                                Instant.now()
                        )
                );
            }

            if (game.status() == GameStatus.WON || game.status() == GameStatus.TIE) {
                publisher.publishGameEnded(
                        new GameEndedEvent(
                                game.id().value(),
                                Instant.now()
                        )
                );
            }
        } catch (PublishAchievementException | RabbitNotReachableException exception) {
            logger.error(exception.getMessage());
        }
    }
}