package be.kdg.team22.checkersservice.api;

import be.kdg.team22.checkersservice.domain.board.exceptions.InvalidMoveException;
import be.kdg.team22.checkersservice.domain.game.exceptions.*;
import be.kdg.team22.checkersservice.domain.player.exceptions.InvalidPlayerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler({
            BoardSizeException.class,
            GameIdException.class,
            GameNotRunningException.class,
            InvalidMoveException.class,
            InvalidPlayerException.class,
            NotPlayersTurnException.class,
            OutsidePlayingFieldException.class,
            PlayerCountException.class,
            RoleUnfulfilledException.class,
            UniquePlayersException.class,
    })
    public ResponseEntity<String> handleDomainErrors(final RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            NotFoundException.class,
    })
    public ResponseEntity<String> handleNotFoundErrors(final RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(final IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(final IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}