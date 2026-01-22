package be.kdg.team22.checkersservice.api;

import be.kdg.team22.checkersservice.api.models.CreateGameModel;
import be.kdg.team22.checkersservice.api.models.GameModel;
import be.kdg.team22.checkersservice.api.models.MoveModel;
import be.kdg.team22.checkersservice.application.GameService;
import be.kdg.team22.checkersservice.domain.game.Game;
import be.kdg.team22.checkersservice.domain.game.GameId;
import be.kdg.team22.checkersservice.domain.player.PlayerId;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
    public ResponseEntity<GameModel> create(@AuthenticationPrincipal final Jwt token,
                                            @Valid @RequestBody final CreateGameModel model) {
        Game game = service.create(PlayerId.get(token), model);
        GameModel gameModel = GameModel.from(game);

        return ResponseEntity.ok(gameModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameModel> get(@AuthenticationPrincipal final Jwt token,
            @PathVariable final UUID id) {
        Game game = service.requestState(new GameId(id), PlayerId.get(token));
        GameModel model = GameModel.from(game);

        return ResponseEntity.ok(model);
    }

    @PostMapping("/{id}/move")
    public ResponseEntity<GameModel> requestMove(@AuthenticationPrincipal final Jwt token,
                                                 @PathVariable final UUID id,
                                                 @Valid @RequestBody final MoveModel moveModel) {
        Game game = service.requestMove(new GameId(id), PlayerId.get(token), moveModel.to());
        GameModel model = GameModel.from(game);

        return ResponseEntity.ok(model);
    }

    @PostMapping("/{id}/move-bot")
    public ResponseEntity<GameModel> requestBotMove(@AuthenticationPrincipal final Jwt token,
                                                    @PathVariable final UUID id) {
        Game game = service.requestBotMove(new GameId(id), PlayerId.get(token));
        GameModel model = GameModel.from(game);
        return ResponseEntity.ok(model);
    }
}