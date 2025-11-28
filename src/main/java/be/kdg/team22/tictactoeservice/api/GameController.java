package be.kdg.team22.tictactoeservice.api;

import be.kdg.team22.tictactoeservice.api.models.CreateGameModel;
import be.kdg.team22.tictactoeservice.api.models.GameModel;
import be.kdg.team22.tictactoeservice.api.models.MoveModel;
import be.kdg.team22.tictactoeservice.application.GameService;
import be.kdg.team22.tictactoeservice.domain.game.Game;
import be.kdg.team22.tictactoeservice.domain.game.GameId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/games")
public class GameController {
    private final GameService service;

    public GameController(GameService service) {
        this.service = service;
    }

    @PostMapping({"/", ""})
    public ResponseEntity<GameModel> createGame(@RequestParam(defaultValue = "false") final boolean aiPlayer, @RequestBody final CreateGameModel model) {
        Game game = service.startGame(model, aiPlayer);
        GameModel gameModel = GameModel.from(game);

        return ResponseEntity.ok(gameModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameModel> getGame(@PathVariable final UUID id) {
        Game game = service.getGame(new GameId(id));
        GameModel model = GameModel.from(game);

        return ResponseEntity.ok(model);
    }

    @PostMapping("/{id}/reset")
    public ResponseEntity<GameModel> resetGame(@PathVariable final UUID id) {
        Game game = service.resetGame(new GameId(id));
        GameModel model = GameModel.from(game);

        return ResponseEntity.ok(model);
    }

    @PostMapping("/{id}/move")
    public ResponseEntity<GameModel> requestMove(@PathVariable final UUID id, @RequestBody final MoveModel moveModel) {
        Game game = service.requestMove(new GameId(id), moveModel.to());
        GameModel model = GameModel.from(game);

        return ResponseEntity.ok(model);
    }
}