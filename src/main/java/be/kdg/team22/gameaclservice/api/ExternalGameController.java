package be.kdg.team22.gameaclservice.api;

import be.kdg.team22.gameaclservice.api.model.CreateChessGameModel;
import be.kdg.team22.gameaclservice.api.model.GameModel;
import be.kdg.team22.gameaclservice.application.chess.GameTranslationService;
import be.kdg.team22.gameaclservice.domain.Game;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/games")
@Validated
public class ExternalGameController {
    private final GameTranslationService translator;

    public ExternalGameController(final GameTranslationService translator) {
        this.translator = translator;
    }

    @PostMapping("/chess")
    public ResponseEntity<GameModel> createChessGame(@RequestBody final CreateChessGameModel request) {
        Game game = translator.startChessGame(request);
        return ResponseEntity.ok(GameModel.from(game));
    }
}
