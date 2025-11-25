package be.kdg.team22.gamesservice.api.game;

import be.kdg.team22.gamesservice.api.game.models.StartGameRequest;
import be.kdg.team22.gamesservice.api.game.models.StartGameResponseModel;
import be.kdg.team22.gamesservice.application.game.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private static final Logger log = LoggerFactory.getLogger(GameController.class);
    private final GameService service;

    public GameController(final GameService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<StartGameResponseModel> startGame(@RequestBody final StartGameRequest request) {
        log.info("Received start-game request for lobby {} and game {}", request.lobbyId(), request.gameId());

        var response = service.startGame(request);

        return ResponseEntity.accepted().body(response);
    }
}