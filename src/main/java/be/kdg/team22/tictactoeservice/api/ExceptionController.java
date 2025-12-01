package be.kdg.team22.tictactoeservice.api;

import be.kdg.team22.tictactoeservice.domain.game.exceptions.*;
import be.kdg.team22.tictactoeservice.domain.player.exceptions.ClaimNotFoundException;
import be.kdg.team22.tictactoeservice.domain.player.exceptions.InvalidPlayerException;
import be.kdg.team22.tictactoeservice.domain.player.exceptions.NotAuthenticatedException;
import be.kdg.team22.tictactoeservice.domain.player.exceptions.PlayerIdentityMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler({
            BoardSizeException.class,
            CellOccupiedException.class,
            GameIdException.class,
            GameNotInProgressException.class,
            GameResetException.class,
            GameSizeException.class,
            InvalidCellException.class,
            NotPlayersTurnException.class,
            RoleUnfulfilledException.class,
            UniquePlayersException.class,
            InvalidPlayerException.class
    })
    public ResponseEntity<String> handleDomainErrors(final RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            NotFoundException.class,
            ClaimNotFoundException.class
    })
    public ResponseEntity<String> handleNotFoundErrors(final RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(AiMoveRequestFailedException.class)
    public ResponseEntity<String> handleAiMoveRequestFailedException(AiMoveRequestFailedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(AiServiceNotReachableException.class)
    public ResponseEntity<String> handleAiServiceNotReachableException(AiServiceNotReachableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(NotAuthenticatedException.class)
    public ResponseEntity<String> handleNotAuthenticated(NotAuthenticatedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PlayerIdentityMismatchException.class)
    public ResponseEntity<String> handleNotAuthenticated(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
