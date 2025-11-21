package be.kdg.team22.tictactoeservice.api;

import be.kdg.team22.tictactoeservice.api.models.GameModel;
import be.kdg.team22.tictactoeservice.api.models.MoveModel;
import be.kdg.team22.tictactoeservice.api.models.PlayersModel;
import be.kdg.team22.tictactoeservice.application.GameService;
import be.kdg.team22.tictactoeservice.domain.game.Game;
import be.kdg.team22.tictactoeservice.domain.game.GameId;
import be.kdg.team22.tictactoeservice.domain.player.Player;
import be.kdg.team22.tictactoeservice.domain.player.PlayerId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/games")
public class GameController {
    private final GameService service;

    public GameController(GameService service) {
        this.service = service;
    }

    @PostMapping({"/", ""})
    public ResponseEntity<GameModel> createGame(@RequestParam(defaultValue = "3") final int size, @RequestBody final PlayersModel playersModel) {
        List<Player> players = new ArrayList<>();
        playersModel.players().forEach((playerId, playerRole) ->
                players.add(new Player(new PlayerId(playerId), playerRole)));

        Game game = service.startGame(size, players);
        GameModel model = GameModel.from(game);

        return ResponseEntity.ok(model);
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