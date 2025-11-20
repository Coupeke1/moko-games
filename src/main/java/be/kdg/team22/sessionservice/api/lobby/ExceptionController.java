package be.kdg.team22.sessionservice.api.lobby;

import be.kdg.team22.sessionservice.domain.lobby.exceptions.GameNotValidException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyCreationException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.OwnerNotValidException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    // 404 Not Found
    @ExceptionHandler(LobbyNotFoundException.class)
    public ResponseEntity<String> handleLobbyNotFound(final LobbyNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    // 400 Bad Request – Application-level fouten (use-case)
    @ExceptionHandler(GameNotValidException.class)
    public ResponseEntity<String> handleGameInvalid(final GameNotValidException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OwnerNotValidException.class)
    public ResponseEntity<String> handleOwnerInvalid(final OwnerNotValidException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LobbyCreationException.class)
    public ResponseEntity<String> handleCreationError(final LobbyCreationException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(final IllegalArgumentException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 400 Bad Request – Domain-level fouten (business rules)
    @ExceptionHandler({
            CannotJoinClosedLobbyException.class,
            PlayerAlreadyInLobbyException.class,
            OwnerCannotLeaveLobbyException.class,
            PlayerNotInLobbyException.class,
            LobbyAlreadyStartedException.class
    })
    public ResponseEntity<String> handleDomainErrors(final RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 500 Internal Server Error – fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnknown(final Exception exception) {
        return new ResponseEntity<>("Internal server error: " + exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
