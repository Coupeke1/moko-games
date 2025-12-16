package be.kdg.team22.checkersservice.api;

import be.kdg.team22.checkersservice.domain.move.exceptions.*;
import be.kdg.team22.checkersservice.domain.game.exceptions.*;
import be.kdg.team22.checkersservice.domain.player.exceptions.ClaimNotFoundException;
import be.kdg.team22.checkersservice.domain.player.exceptions.InvalidPlayerException;
import be.kdg.team22.checkersservice.domain.player.exceptions.PlayerIdentityMismatchException;
import be.kdg.team22.checkersservice.domain.player.exceptions.PlayerNotInThisGameException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler({
            BackwardsMoveException.class,
            BoardSizeException.class,
            CapturedPieceNotOnLastTileException.class,
            GameIdException.class,
            GameNotRunningException.class,
            GameResetException.class,
            InvalidPlayerException.class,
            MoveNotDiagonalException.class,
            NotPlayersPieceException.class,
            NotPlayersTurnException.class,
            OutsidePlayingFieldException.class,
            OwnPieceInTheWayException.class,
            PlayerCountException.class,
            RoleUnfulfilledException.class,
            TargetCellNotEmptyException.class,
            TooManyMovesException.class,
            TooManyPiecesException.class,
            TooManyTilesException.class,
            UniquePlayersException.class,
    })
    public ResponseEntity<String> handleDomainErrors(final RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            ClaimNotFoundException.class,
            NotFoundException.class,
            StartingPieceNotFoundException.class
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