package be.kdg.team22.gameaclservice.application.chess;

import be.kdg.team22.gameaclservice.api.model.CreateChessGameModel;
import be.kdg.team22.gameaclservice.config.ChessInfoProperties;
import be.kdg.team22.gameaclservice.domain.Game;
import be.kdg.team22.gameaclservice.events.inbound.ChessAchievementEvent;
import be.kdg.team22.gameaclservice.events.inbound.ChessRegisterEvent;
import be.kdg.team22.gameaclservice.events.outbound.GameAchievementEvent;
import be.kdg.team22.gameaclservice.infrastructure.external.chess.ChessGameResponse;
import be.kdg.team22.gameaclservice.infrastructure.external.chess.CreateChessGameRequest;
import be.kdg.team22.gameaclservice.infrastructure.external.chess.ExternalChessGame;
import be.kdg.team22.gameaclservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.gameaclservice.infrastructure.games.RegisterGameRequest;
import be.kdg.team22.gameaclservice.infrastructure.messaging.AchievementEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class GameTranslationService {
    private final AchievementEventPublisher achievementEventPublisher;
    private final ExternalGamesRepository gameRepository;
    private final ExternalChessGame chessGame;
    private final UserService userService;
    private final ChessInfoProperties chessInfoProperties;
    @Value("${acl-config.backend-url}")
    private String aclBackendUrl;
    private final Logger logger = LoggerFactory.getLogger(GameTranslationService.class);

    public GameTranslationService(final AchievementEventPublisher achievementEventPublisher, final ExternalGamesRepository gameRepository, final ExternalChessGame chessGame, final UserService userService, final  ChessInfoProperties chessInfoProperties) {
        this.achievementEventPublisher = achievementEventPublisher;
        this.gameRepository = gameRepository;
        this.chessGame = chessGame;
        this.userService = userService;
        this.chessInfoProperties = chessInfoProperties;
    }

    public void translateAndRegisterGame(ChessRegisterEvent event) {
        logger.info("Translating and registering chess");
        RegisterGameRequest convertedRequest = RegisterGameRequest.convert(event, chessInfoProperties, aclBackendUrl);
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

    public Game startChessGame(CreateChessGameModel request) {
        Game game = Game.create(request.players());
        Map<UUID, String> players = userService.resolveUsernames(game.players());

        ChessGameResponse response = chessGame.createGame(game.id(), CreateChessGameRequest.convert(players));
        game.setId(response.data().gameId());
        return game;
    }
}
