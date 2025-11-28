package be.kdg.team22.tictactoeservice.application;

import be.kdg.team22.tictactoeservice.api.models.CreateGameModel;
import be.kdg.team22.tictactoeservice.application.events.GameEventPublisher;
import be.kdg.team22.tictactoeservice.config.BoardSizeProperties;
import be.kdg.team22.tictactoeservice.domain.events.GameDrawEvent;
import be.kdg.team22.tictactoeservice.domain.events.GameWonEvent;
import be.kdg.team22.tictactoeservice.domain.events.exceptions.PublishAchievementException;
import be.kdg.team22.tictactoeservice.domain.events.exceptions.RabbitNotReachableException;
import be.kdg.team22.tictactoeservice.domain.game.Game;
import be.kdg.team22.tictactoeservice.domain.game.GameId;
import be.kdg.team22.tictactoeservice.domain.game.GameStatus;
import be.kdg.team22.tictactoeservice.domain.game.Move;
import be.kdg.team22.tictactoeservice.domain.player.PlayerId;
import be.kdg.team22.tictactoeservice.infrastructure.game.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class GameService {
    private final GameRepository repository;
    private final BoardSizeProperties config;
    private final GameEventPublisher publisher;

    private final Logger logger;

    public GameService(GameRepository repository, BoardSizeProperties config, GameEventPublisher publisher) {
        this.repository = repository;
        this.config = config;
        this.publisher = publisher;
        logger = LoggerFactory.getLogger(GameService.class);
    }

    public Game startGame(final CreateGameModel model) {
        List<PlayerId> players = model.players().stream().map(PlayerId::new).toList();
        Game game = Game.create(config.minSize(), config.maxSize(), model.settings().boardSize(), players);
        repository.save(game);
        return game;
    }

    public Game getGame(final GameId id) {
        return repository.findById(id).orElseThrow(id::notFound);
    }

    public Game resetGame(final GameId id) {
        Game game = getGame(id);
        game.reset();
        repository.save(game);
        return game;
    }

    public Game requestMove(final GameId id, final Move move) {
        Game game = getGame(id);
        game.requestMove(move);
        repository.save(game);

        try {
            if (game.status() == GameStatus.WON) {
                publisher.publishGameWon(
                        new GameWonEvent(
                                game.id().value(),
                                game.winner().value(),
                                Instant.now()
                        )
                );
            }

            if (game.status() == GameStatus.TIE) {
                publisher.publishGameDraw(
                        new GameDrawEvent(
                                game.id().value(),
                                game.players().stream()
                                        .map(p -> p.id().value())
                                        .toList(),
                                Instant.now()
                        )
                );
            }
        } catch (PublishAchievementException | RabbitNotReachableException exception) {
            logger.error(exception.getMessage());
        }

        return game;
    }
}