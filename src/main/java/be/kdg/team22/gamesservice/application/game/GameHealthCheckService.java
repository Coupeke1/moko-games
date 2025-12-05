package be.kdg.team22.gamesservice.application.game;

import be.kdg.team22.gamesservice.domain.game.Game;
import be.kdg.team22.gamesservice.domain.game.GameRepository;
import be.kdg.team22.gamesservice.infrastructure.game.health.GameHealthChecker;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameHealthCheckService {
    private final GameRepository gameRepository;
    private final GameHealthChecker gameHealthChecker;

    public GameHealthCheckService(GameRepository gameRepository,  GameHealthChecker gameHealthChecker) {
        this.gameRepository = gameRepository;
        this.gameHealthChecker = gameHealthChecker;
    }

    @Scheduled(fixedRateString = "${games.health-check.interval}")
    public void checkAllGamesHealth() {
        List<Game> games = gameRepository.findAll();
        games.forEach(game -> {
            try {
                boolean isHealthy = gameHealthChecker.isHealthy(game);
                game.updateHealthStatus(isHealthy);
                gameRepository.save(game);
            } catch (Exception e) {
                game.updateHealthStatus(false);
                gameRepository.save(game);
            }
        });
    }
}
