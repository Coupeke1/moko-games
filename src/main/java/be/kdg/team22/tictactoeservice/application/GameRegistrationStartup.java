package be.kdg.team22.tictactoeservice.application;

import be.kdg.team22.tictactoeservice.config.GameInfoProperties;
import be.kdg.team22.tictactoeservice.domain.register.exceptions.GameNotRegisteredException;
import be.kdg.team22.tictactoeservice.infrastructure.register.ExternalRegisterRepository;
import be.kdg.team22.tictactoeservice.infrastructure.register.RegisterAchievementRequest;
import be.kdg.team22.tictactoeservice.infrastructure.register.RegisterGameRequest;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

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
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ignored) {}

        List<RegisterAchievementRequest> achievementRequests = gameInfo.achievements().stream().map(achievement -> new RegisterAchievementRequest(
                achievement.key(),
                achievement.name(),
                achievement.description(),
                achievement.levels()
        )).toList();

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
                achievementRequests
        );

        boolean created = externalRegisterRepository.registerGame(request);
        if (!created) throw new GameNotRegisteredException();
    }
}
