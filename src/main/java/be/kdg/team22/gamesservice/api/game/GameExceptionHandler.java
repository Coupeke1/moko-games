package be.kdg.team22.gamesservice.api.game;

import be.kdg.team22.gamesservice.domain.game.exceptions.*;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GameExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GameExceptionHandler.class);

    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<String> handleGameNotFound(final GameNotFoundException ex) {
        log.warn("Game not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(AchievementNotFoundException.class)
    public ResponseEntity<String> handleAchievementNotFound(final AchievementNotFoundException ex) {
        log.warn("Achievement not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidGameSettingsException.class)
    public ResponseEntity<String> handleInvalidGameSettings(final InvalidGameSettingsException ex) {
        log.warn("Invalid game settings: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(PlayersListEmptyException.class)
    public ResponseEntity<String> handleEmptyPlayers(final PlayersListEmptyException ex) {
        log.warn("Invalid start request: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleJsonError(final HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof InvalidTypeIdException invalidType) {
            String message = "Unsupported game settings type: " + invalidType.getTypeId();
            log.warn(message);
            return ResponseEntity.badRequest().body(message);
        }

        log.warn("Malformed JSON: {}", ex.getMessage());
        return ResponseEntity.badRequest().body("Malformed JSON request");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(final IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(DuplicateGameNameException.class)
    public ResponseEntity<String> handleDuplicateGameName(final DuplicateGameNameException ex) {
        log.warn("Duplicate game: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(GameUnhealthyException.class)
    public ResponseEntity<String> handleUnhealthyGameName(final GameUnhealthyException ex) {
        log.warn("Unhealthy game: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}