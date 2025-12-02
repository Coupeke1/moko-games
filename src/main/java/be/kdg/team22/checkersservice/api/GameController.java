package be.kdg.team22.checkersservice.api;

import be.kdg.team22.checkersservice.api.models.CreateGameModel;
import be.kdg.team22.checkersservice.api.models.GameModel;
import be.kdg.team22.checkersservice.application.GameService;
import be.kdg.team22.checkersservice.domain.game.Game;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
@Validated
public class GameController {
    private final GameService service;

    public GameController(GameService service) {
        this.service = service;
    }

    @PostMapping({"/", ""})
    public ResponseEntity<GameModel> create(@RequestParam(defaultValue = "false") final boolean aiPlayer, @RequestBody final CreateGameModel model) {
        Game game = service.create(model, aiPlayer);
        GameModel gameModel = GameModel.from(game);

        return ResponseEntity.ok(gameModel);
    }
}