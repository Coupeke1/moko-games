package be.kdg.team22.checkersservice.api;

import be.kdg.team22.checkersservice.api.models.CreateGameModel;
import be.kdg.team22.checkersservice.api.models.GameModel;
import be.kdg.team22.checkersservice.api.models.MoveModel;
import be.kdg.team22.checkersservice.application.GameService;
import be.kdg.team22.checkersservice.domain.game.Game;
import be.kdg.team22.checkersservice.domain.game.GameId;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/games")
@Validated
public class GameController {
    private final GameService service;

    public GameController(final GameService service) {
        this.service = service;
    }

    @PostMapping({"/", ""})
    public ResponseEntity<GameModel> create(@RequestParam(defaultValue = "false") final boolean aiPlayer, @Valid @RequestBody final CreateGameModel model) {
        Game game = service.create(model, aiPlayer);
        GameModel gameModel = GameModel.from(game);

        return ResponseEntity.ok(gameModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameModel> get(@PathVariable final UUID id) {
        Game game = service.getById(new GameId(id));
        GameModel model = GameModel.from(game);

        return ResponseEntity.ok(model);
    }

    @PostMapping("/{id}/move")
    public ResponseEntity<GameModel> requestMove(@PathVariable final UUID id,
                                                 @Valid @RequestBody final MoveModel moveModel) {
        Game game = service.requestMove(new GameId(id), moveModel.to());
        GameModel model = GameModel.from(game);

        return ResponseEntity.ok(model);
    }

    @PostMapping("/{id}/reset")
    public ResponseEntity<GameModel> resetGame(@PathVariable final UUID id) {
        Game game = service.reset(new GameId(id));
        GameModel model = GameModel.from(game);

        return ResponseEntity.ok(model);
    }
}