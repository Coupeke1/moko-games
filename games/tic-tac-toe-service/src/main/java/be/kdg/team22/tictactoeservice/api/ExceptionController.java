package be.kdg.team22.tictactoeservice.api;

import be.kdg.team22.tictactoeservice.domain.game.exceptions.*;
import be.kdg.team22.tictactoeservice.domain.player.exceptions.ClaimNotFoundException;
import be.kdg.team22.tictactoeservice.domain.player.exceptions.InvalidPlayerException;
import be.kdg.team22.tictactoeservice.domain.player.exceptions.PlayerIdentityMismatchException;
import be.kdg.team22.tictactoeservice.domain.player.exceptions.PlayerNotInThisGameException;
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
            GameSizeException.class,
            InvalidCellException.class,
            NotPlayersTurnException.class,
            RoleUnfulfilledException.class,
            UniquePlayersException.class,
            InvalidPlayerException.class
    })
    public ResponseEntity<String> handleDomainErrors(final RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            NotFoundException.class,
            ClaimNotFoundException.class
    })
    public ResponseEntity<String> handleNotFoundErrors(final RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(final IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(final IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler({
            BotMoveRequestFailedException.class,
            BotServiceNotReachableException.class
    })
    public ResponseEntity<String> handleBotExceptions(final RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler({
            PlayerIdentityMismatchException.class,
            PlayerNotInThisGameException.class
    })
    public ResponseEntity<String> handlePlayerAuthExceptions(final RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }
}
