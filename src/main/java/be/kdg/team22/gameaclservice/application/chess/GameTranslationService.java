package be.kdg.team22.gameaclservice.application.chess;

import be.kdg.team22.gameaclservice.api.model.CreateChessGameModel;
import be.kdg.team22.gameaclservice.config.ChessInfoProperties;
import be.kdg.team22.gameaclservice.domain.Game;
import be.kdg.team22.gameaclservice.events.inbound.ChessAchievementEvent;
import be.kdg.team22.gameaclservice.events.inbound.ChessGameEndedEvent;
import be.kdg.team22.gameaclservice.events.inbound.ChessRegisterEvent;
import be.kdg.team22.gameaclservice.events.outbound.GameAchievementEvent;
import be.kdg.team22.gameaclservice.events.outbound.GameEndedEvent;
import be.kdg.team22.gameaclservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.gameaclservice.infrastructure.games.RegisterGameRequest;
import be.kdg.team22.gameaclservice.infrastructure.messaging.AchievementEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GameTranslationService {
    private final AchievementEventPublisher achievementEventPublisher;
    private final ExternalGamesRepository gameRepository;
    private final ChessInfoProperties chessInfoProperties;
    @Value("${acl-config.backend-url}")
    private String aclBackendUrl;
    private final Logger logger = LoggerFactory.getLogger(GameTranslationService.class);

    public GameTranslationService(final AchievementEventPublisher achievementEventPublisher, final ExternalGamesRepository gameRepository, final  ChessInfoProperties chessInfoProperties) {
        this.achievementEventPublisher = achievementEventPublisher;
        this.gameRepository = gameRepository;
        this.chessInfoProperties = chessInfoProperties;
    }

    public void translateAndRegisterGame(final ChessRegisterEvent event) {
        logger.info("Translating and registering chess");
        RegisterGameRequest convertedRequest = RegisterGameRequest.convert(event, chessInfoProperties, aclBackendUrl);
        logger.info("Registering game");
        gameRepository.registerGame(convertedRequest);
        logger.info("Game registered");
    }

    public void translateAndSendAchievement(final ChessAchievementEvent event) {
        logger.info("Translating and awarding achievement for chess with type {} for player {}", event.achievementType(), event.playerId());
        GameAchievementEvent convertedEvent = GameAchievementEvent.convert(event, "chess");
        logger.info("Publishing achievement event");
        achievementEventPublisher.publishAchievementEvent(convertedEvent);
        logger.info("Achievement event published");
    }

    public void translateAndSendGameEnded(final ChessGameEndedEvent event) {
        logger.info("Translating and handling game ended event for chess game {}", event.gameId());
        GameEndedEvent convertedEvent = GameEndedEvent.convert(event);
        logger.info("Publishing game ended event");
        achievementEventPublisher.publishGameEndedEvent(convertedEvent);
        logger.info("Game ended event published");
    }

    public Game startChessGame(final CreateChessGameModel request) {
        return Game.create(request.players());
    }
}
