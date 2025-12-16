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
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler({
            AiMoveRequestFailedException.class,
            AiServiceNotReachableException.class
    })
    public ResponseEntity<String> handleBotExceptions(AiMoveRequestFailedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler({
            PlayerIdentityMismatchException.class,
            PlayerNotInThisGameException.class
    })
    public ResponseEntity<String> handlePlayerAuthExceptions(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }
}
