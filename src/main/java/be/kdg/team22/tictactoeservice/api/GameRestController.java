package be.kdg.team22.tictactoeservice.api;

import be.kdg.team22.tictactoeservice.api.models.GameModel;
import be.kdg.team22.tictactoeservice.application.GameService;
import be.kdg.team22.tictactoeservice.domain.Game;
import be.kdg.team22.tictactoeservice.domain.GameId;
import be.kdg.team22.tictactoeservice.domain.PlayerId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/games")
public class GameRestController {
    private final GameService service;

    public GameRestController(GameService service) {
        this.service = service;
    }

    @PostMapping({"/", ""})
    public ResponseEntity<GameModel> createGame(@RequestParam(defaultValue = "3") int size) {
        //TODO: inject player ids with keycloak
        Game game = service.startGame(size, PlayerId.create(), PlayerId.create());
        GameModel model = GameModel.fromDomain(game);

        return ResponseEntity.ok(model);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameModel> getGame(@PathVariable UUID id) {
        Game game = service.getGame(new GameId(id));
        GameModel model = GameModel.fromDomain(game);

        return ResponseEntity.ok(model);
    }

    @PostMapping("/{id}/reset")
    public ResponseEntity<GameModel> resetGame(@PathVariable UUID id) {
        Game game = service.resetGame(new GameId(id));
        GameModel model = GameModel.fromDomain(game);

        return ResponseEntity.ok(model);
    }
}
