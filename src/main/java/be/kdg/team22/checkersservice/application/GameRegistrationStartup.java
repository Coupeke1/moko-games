package be.kdg.team22.checkersservice.application;

import be.kdg.team22.checkersservice.config.GameInfoProperties;
import be.kdg.team22.checkersservice.domain.register.exceptions.GameNotRegisteredException;
import be.kdg.team22.checkersservice.domain.register.exceptions.GameServiceNotReachableException;
import be.kdg.team22.checkersservice.infrastructure.register.ExternalRegisterRepository;
import be.kdg.team22.checkersservice.infrastructure.register.RegisterAchievementRequest;
import be.kdg.team22.checkersservice.infrastructure.register.RegisterGameRequest;
import be.kdg.team22.checkersservice.infrastructure.register.RegisterSettingsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameRegistrationStartup {
    private static final Logger logger = LoggerFactory.getLogger(GameRegistrationStartup.class);
    private final ExternalRegisterRepository externalRegisterRepository;
    private final GameInfoProperties gameInfo;

    private final static int MAX_RETRIES = 3;
    private final static int RETRY_DELAY_MS = 2000;

    public GameRegistrationStartup(final ExternalRegisterRepository externalRegisterRepository,
                                   final GameInfoProperties gameInfoProperties) {
        this.externalRegisterRepository = externalRegisterRepository;
        this.gameInfo = gameInfoProperties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registerGameOnStartup() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ignored) {
        }

        List<RegisterAchievementRequest> achievementRequests =
                gameInfo.achievements().stream()
                        .map(a -> new RegisterAchievementRequest(a.key(), a.name(), a.description(), a.levels()))
                        .toList();

        RegisterGameRequest.GameSettingsDefinition settingsDefinition = null;
        if (gameInfo.settingsDefinition() != null && gameInfo.settingsDefinition().settings() != null) {
            List<RegisterSettingsRequest> defs = gameInfo.settingsDefinition().settings().stream()
                    .map(s -> new RegisterSettingsRequest(
                            s.name(),
                            s.type(),
                            s.required(),
                            s.min(),
                            s.max(),
                            s.allowedValues(),
                            s.defaultValue()
                    ))
                    .toList();

            settingsDefinition = new RegisterGameRequest.GameSettingsDefinition(defs);
        }

        RegisterGameRequest request = new RegisterGameRequest(
                gameInfo.name(),
                gameInfo.backendUrl(),
                gameInfo.frontendUrl(),
                gameInfo.startEndpoint(),
                gameInfo.healthEndpoint(),
                gameInfo.title(),
                gameInfo.description(),
                gameInfo.image(),
                gameInfo.price(),
                gameInfo.category(),
                settingsDefinition,
                achievementRequests
        );

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                boolean created = externalRegisterRepository.registerGame(request);
                if (created) {
                    logger.info("Game '{}' successfully registered in the game service.", gameInfo.name());
                    return;
                }
                if (attempt < MAX_RETRIES) {
                    logger.warn("Game registration attempt {} failed. Retrying...", attempt);
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ignored) {
                    }
                }
            } catch (GameServiceNotReachableException e) {
                if (attempt < MAX_RETRIES) {
                    logger.warn("Game service not reachable on attempt {}. Retrying...", attempt);
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ignored) {
                    }
                } else {
                    throw e;
                }
            }
        }
        throw new GameNotRegisteredException();
    }
}
