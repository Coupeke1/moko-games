package be.kdg.team22.gameaclservice.application;

import be.kdg.team22.gameaclservice.config.ChessInfoProperties;
import be.kdg.team22.gameaclservice.events.inbound.ChessAchievementEvent;
import be.kdg.team22.gameaclservice.events.inbound.ChessRegisterEvent;
import be.kdg.team22.gameaclservice.events.outbound.GameAchievementEvent;
import be.kdg.team22.gameaclservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.gameaclservice.infrastructure.games.RegisterGameRequest;
import be.kdg.team22.gameaclservice.infrastructure.messaging.AchievementEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TranslationService {
    private final AchievementEventPublisher achievementEventPublisher;
    private final ExternalGamesRepository gameRepository;
    private final ChessInfoProperties chessInfoProperties;

    public TranslationService(final AchievementEventPublisher achievementEventPublisher, final ExternalGamesRepository gameRepository, final  ChessInfoProperties chessInfoProperties) {
        this.achievementEventPublisher = achievementEventPublisher;
        this.gameRepository = gameRepository;
        this.chessInfoProperties = chessInfoProperties;
    }

    public void translateAndRegisterGame(ChessRegisterEvent event) {
        RegisterGameRequest convertedRequest = RegisterGameRequest.convert(event, chessInfoProperties);
        gameRepository.registerGame(convertedRequest);
    }

    public void translateAndSendAchievement(ChessAchievementEvent event) {
        GameAchievementEvent convertedEvent = GameAchievementEvent.convert(event, "chess");
        achievementEventPublisher.publishAchievementEvent(convertedEvent);
    }
}
