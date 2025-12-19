package be.kdg.team22.gameaclservice.application;

import be.kdg.team22.gameaclservice.config.ChessInfoProperties;
import be.kdg.team22.gameaclservice.events.inbound.ChessAchievementEvent;
import be.kdg.team22.gameaclservice.events.inbound.ChessRegisterEvent;
import be.kdg.team22.gameaclservice.events.outbound.GameAchievementEvent;
import be.kdg.team22.gameaclservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.gameaclservice.infrastructure.games.RegisterGameRequest;
import be.kdg.team22.gameaclservice.infrastructure.messaging.AchievementEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TranslationService {
    private final AchievementEventPublisher achievementEventPublisher;
    private final ExternalGamesRepository gameRepository;
    private final ChessInfoProperties chessInfoProperties;
    private final Logger logger = LoggerFactory.getLogger(TranslationService.class);

    public TranslationService(final AchievementEventPublisher achievementEventPublisher, final ExternalGamesRepository gameRepository, final  ChessInfoProperties chessInfoProperties) {
        this.achievementEventPublisher = achievementEventPublisher;
        this.gameRepository = gameRepository;
        this.chessInfoProperties = chessInfoProperties;
    }

    public void translateAndRegisterGame(ChessRegisterEvent event) {
        logger.info("Translating and registering chess");
        RegisterGameRequest convertedRequest = RegisterGameRequest.convert(event, chessInfoProperties);
        logger.info("Registering game");
        gameRepository.registerGame(convertedRequest);
        logger.info("Game registered");
    }

    public void translateAndSendAchievement(ChessAchievementEvent event) {
        logger.info("Translating and awarding achievement for chess with type {} for player {}", event.achievementType(), event.playerId());
        GameAchievementEvent convertedEvent = GameAchievementEvent.convert(event, "chess");
        logger.info("Publishing achievement event");
        achievementEventPublisher.publishAchievementEvent(convertedEvent);
        logger.info("Achievement event published");
    }
}
