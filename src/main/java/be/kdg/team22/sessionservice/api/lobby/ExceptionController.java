package be.kdg.team22.sessionservice.api.lobby;

import be.kdg.team22.sessionservice.domain.lobby.exceptions.*;
import be.kdg.team22.sessionservice.infrastructure.lobby.db.exceptions.SettingsConversionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(LobbyNotFoundException.class)
    public ResponseEntity<String> handleLobbyNotFound(final LobbyNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

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

    @ExceptionHandler({
            CannotJoinClosedLobbyException.class,
            PlayerAlreadyInLobbyException.class,
            OwnerCannotLeaveLobbyException.class,
            PlayerNotInLobbyException.class,
            LobbyAlreadyStartedException.class,
            NotLobbyOwnerException.class,
            LobbyManagementNotAllowedException.class,
            MaxPlayersTooSmallException.class
    })
    public ResponseEntity<String> handleDomainErrors(final RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SettingsConversionException.class)
    public ResponseEntity<String> handleSettingsConversion(final SettingsConversionException exception) {
        return new ResponseEntity<>(
                "Settings conversion error: " + exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnknown(final Exception exception) {
        return new ResponseEntity<>(
                "Internal server error: " + exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(LobbySettingsInvalidException.class)
    public ResponseEntity<String> handleLobbySettingsInvalid(final LobbySettingsInvalidException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
