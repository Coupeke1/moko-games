package be.kdg.team22.gamesservice.api.game;

import be.kdg.team22.gamesservice.api.game.models.*;
import be.kdg.team22.gamesservice.application.game.GameService;
import be.kdg.team22.gamesservice.domain.game.Achievement;
import be.kdg.team22.gamesservice.domain.game.AchievementKey;
import be.kdg.team22.gamesservice.domain.game.Game;
import be.kdg.team22.gamesservice.domain.game.GameId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/games")
public class GameController {
    private static final Logger log = LoggerFactory.getLogger(GameController.class);
    private final GameService service;

    public GameController(final GameService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<StartGameResponseModel> startGame(@AuthenticationPrincipal final Jwt token,
                                                            @RequestBody final StartGameRequest request) {
        log.info("Received start-game request for lobby {} and game {}", request.lobbyId(), request.gameId());

        StartGameResponseModel response = service.startGame(request, token);

        return ResponseEntity.accepted().body(response);
    }

    @GetMapping
    public ResponseEntity<GameListModel> getAllGames() {
        List<Game> games = service.findAll();

        List<GameDetailsModel> models = games.stream()
                .map(GameDetailsModel::from)
                .toList();

        return ResponseEntity.ok(new GameListModel(models));
    }

    @GetMapping("/{value}")
    public ResponseEntity<GameDetailsModel> getGame(final @PathVariable String value) {
        try {
            GameId id = GameId.from(value);
            Game game = service.findById(id);
            return ResponseEntity.ok(GameDetailsModel.from(game));
        } catch (IllegalArgumentException ignored) {
        }

        Game game = service.findByName(value);
        return ResponseEntity.ok(GameDetailsModel.from(game));
    }

    @PutMapping("/{name}")
    public ResponseEntity<GameDetailsModel> registerGame(final @PathVariable String name, final @RequestBody RegisterGameRequest request) {
        Game game = service.register(name, request);
        return ResponseEntity.ok(GameDetailsModel.from(game));
    }

    @PostMapping("/register")
    public ResponseEntity<GameDetailsModel> createGame(final @RequestBody RegisterGameRequest request) {
        Game game = service.create(request);
        return ResponseEntity.ok(GameDetailsModel.from(game));
    }

    @GetMapping("/{id}/achievements")
    public ResponseEntity<AchievementListModel> getAchievements(final @PathVariable UUID id) {
        List<Achievement> achievements = service.getAchievements(GameId.from(id));
        List<AchievementDetailsModel> detailsModels = achievements.stream().map(AchievementDetailsModel::from).toList();

        return ResponseEntity.ok(new AchievementListModel(detailsModels));
    }

    @GetMapping("/{id}/achievements/{key}")
    public ResponseEntity<AchievementDetailsModel> getAchievement(final @PathVariable UUID id, final @PathVariable String key) {
        Achievement achievement = service.getAchievement(GameId.from(id), AchievementKey.fromString(key));
        return ResponseEntity.ok(AchievementDetailsModel.from(achievement));
    }

    @GetMapping("/{id}/settings")
    public ResponseEntity<GameSettingsSchemaModel> getGameSettingsSchema(@PathVariable UUID id) {
        Game game = service.findById(GameId.from(id));

        return ResponseEntity.ok(GameSettingsSchemaModel.from(game.settingsDefinition()));
    }
}