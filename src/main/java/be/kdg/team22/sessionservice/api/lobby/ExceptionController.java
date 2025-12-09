package be.kdg.team22.sessionservice.api.lobby;

import be.kdg.team22.sessionservice.domain.game.GameServiceNotReachableException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.*;
import be.kdg.team22.sessionservice.domain.player.exceptions.PlayerAlreadyInLobbyException;
import be.kdg.team22.sessionservice.domain.player.exceptions.PlayerNotFoundException;
import be.kdg.team22.sessionservice.domain.player.exceptions.PlayerNotInLobbyException;
import be.kdg.team22.sessionservice.infrastructure.lobby.jpa.exceptions.SettingsConversionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler({ClaimNotFoundException.class, LobbyNotFoundException.class, PlayerNotFoundException.class, OwnerNotFoundException.class, InviteNotFoundException.class, GameNotFoundException.class, LobbyNotFoundByGameInstanceException.class})
    public ResponseEntity<String> handleNotFound(final RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({GameNotValidException.class, OwnerNotValidException.class, LobbyCreationException.class, IllegalArgumentException.class, CannotJoinClosedLobbyException.class, PlayerAlreadyInLobbyException.class, OwnerCannotLeaveLobbyException.class, PlayerNotInLobbyException.class, LobbyAlreadyStartedException.class, NotLobbyOwnerException.class, LobbyManagementNotAllowedException.class, MaxPlayersTooSmallException.class, LobbySettingsInvalidException.class, PlayersNotReadyException.class, TooManyBotsException.class, PlayersException.class, UnsupportedOperationException.class, LobbyNotStartedException.class})
    public ResponseEntity<String> handleBadRequest(final RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NotReachableException.class, GameServiceNotReachableException.class})
    public ResponseEntity<String> handleServiceUnreachable(final RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(SettingsConversionException.class)
    public ResponseEntity<String> handleSettingsConversion(final SettingsConversionException exception) {
        return new ResponseEntity<>("Settings conversion error: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnknown(final Exception exception) {
        return new ResponseEntity<>("Internal server error: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
