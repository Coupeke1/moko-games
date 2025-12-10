package be.kdg.team22.checkersservice.application;

import be.kdg.team22.checkersservice.config.GameInfoProperties;
import be.kdg.team22.checkersservice.domain.register.GameRegisterId;
import be.kdg.team22.checkersservice.domain.register.exceptions.GameNotRegisteredException;
import be.kdg.team22.checkersservice.infrastructure.register.ExternalRegisterRepository;
import be.kdg.team22.checkersservice.infrastructure.register.GameResponse;
import be.kdg.team22.checkersservice.infrastructure.register.RegisterGameRequest;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class GameRegistrationStartup {
    private final ExternalRegisterRepository externalRegisterRepository;
    private final GameInfoProperties gameInfo;

    public GameRegistrationStartup(final ExternalRegisterRepository externalRegisterRepository, final GameInfoProperties gameInfoProperties) {
        this.externalRegisterRepository = externalRegisterRepository;
        this.gameInfo = gameInfoProperties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registerGameOnStartup() {
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
                gameInfo.category()
        );

        GameResponse existingGame = externalRegisterRepository.getGame(gameInfo.name());
        if (existingGame != null) {
            externalRegisterRepository.updateGame(GameRegisterId.fromUuid(existingGame.id()), request);
            return;
        }

        boolean created = externalRegisterRepository.registerGame(request);
        if (!created) throw new GameNotRegisteredException();
    }
}
