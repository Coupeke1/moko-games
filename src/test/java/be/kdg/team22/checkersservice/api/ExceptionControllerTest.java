package be.kdg.team22.checkersservice.api;

import be.kdg.team22.checkersservice.domain.game.exceptions.*;
import be.kdg.team22.checkersservice.domain.move.exceptions.*;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;
import be.kdg.team22.checkersservice.domain.player.exceptions.ClaimNotFoundException;
import be.kdg.team22.checkersservice.domain.player.exceptions.InvalidPlayerException;
import be.kdg.team22.checkersservice.domain.player.exceptions.PlayerIdentityMismatchException;
import be.kdg.team22.checkersservice.domain.player.exceptions.PlayerNotInThisGameException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ExceptionControllerTest {

    private final ExceptionController controller = new ExceptionController();

    @Test
    void handleDomainExceptions() {
        BackwardsMoveException backwardsMoveEx = new BackwardsMoveException(PlayerRole.WHITE);
        BoardSizeException boardSizeEx = new BoardSizeException();
        CapturedPieceNotOnLastTileException capturedPieceNotOnLastTileEx = new CapturedPieceNotOnLastTileException();
        GameIdException gameIdEx = new GameIdException();
        GameNotRunningException gameNotRunningEx = new GameNotRunningException();
        GameResetException gameResetEx = new GameResetException();
        InvalidPlayerException invalidPlayerEx = new InvalidPlayerException();
        MoveNotDiagonalException moveNotDiagonalEx = new MoveNotDiagonalException();
        NotPlayersPieceException notPlayersPieceEx = new NotPlayersPieceException(1);
        NotPlayersTurnException notPlayersTurnEx = new NotPlayersTurnException(PlayerRole.WHITE);
        OutsidePlayingFieldException outsidePlayingFieldEx = new OutsidePlayingFieldException();
        OwnPieceInTheWayException ownPieceInTheWayEx = new OwnPieceInTheWayException();
        PlayerCountException playerCountEx = new PlayerCountException();
        RoleUnfulfilledException roleUnfulfilledEx = new RoleUnfulfilledException(PlayerRole.BLACK);
        TooManyMovesException tooManyMovesEx = new TooManyMovesException();
        TooManyPiecesException tooManyPiecesEx = new TooManyPiecesException();
        TooManyTilesException tooManyTilesEx = new TooManyTilesException(2, true);
        UniquePlayersException uniquePlayersEx = new UniquePlayersException();

        ResponseEntity<String> backwardsMoveExResponse = controller.handleDomainErrors(backwardsMoveEx);
        ResponseEntity<String> boardSizeExResponse = controller.handleDomainErrors(boardSizeEx);
        ResponseEntity<String> capturedPieceNotOnLastTileExResponse = controller.handleDomainErrors(capturedPieceNotOnLastTileEx);
        ResponseEntity<String> gameIdExResponse = controller.handleDomainErrors(gameIdEx);
        ResponseEntity<String> gameNotRunningExResponse = controller.handleDomainErrors(gameNotRunningEx);
        ResponseEntity<String> gameResetExResponse = controller.handleDomainErrors(gameResetEx);
        ResponseEntity<String> invalidPlayerExResponse = controller.handleDomainErrors(invalidPlayerEx);
        ResponseEntity<String> moveNotDiagonalExResponse = controller.handleDomainErrors(moveNotDiagonalEx);
        ResponseEntity<String> notPlayersPieceExResponse = controller.handleDomainErrors(notPlayersPieceEx);
        ResponseEntity<String> notPlayersTurnExResponse = controller.handleDomainErrors(notPlayersTurnEx);
        ResponseEntity<String> outsidePlayingFieldExResponse = controller.handleDomainErrors(outsidePlayingFieldEx);
        ResponseEntity<String> ownPieceInTheWayExResponse = controller.handleDomainErrors(ownPieceInTheWayEx);
        ResponseEntity<String> playerCountExResponse = controller.handleDomainErrors(playerCountEx);
        ResponseEntity<String> roleUnfulfilledExResponse = controller.handleDomainErrors(roleUnfulfilledEx);
        ResponseEntity<String> tooManyMovesExResponse = controller.handleDomainErrors(tooManyMovesEx);
        ResponseEntity<String> tooManyPiecesExResponse = controller.handleDomainErrors(tooManyPiecesEx);
        ResponseEntity<String> tooManyTilesExResponse = controller.handleDomainErrors(tooManyTilesEx);
        ResponseEntity<String> uniquePlayersExResponse = controller.handleDomainErrors(uniquePlayersEx);

        assertThat(backwardsMoveExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(boardSizeExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(capturedPieceNotOnLastTileExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(gameIdExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(gameNotRunningExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(gameResetExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(invalidPlayerExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(moveNotDiagonalExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(notPlayersPieceExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(notPlayersTurnExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(outsidePlayingFieldExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(ownPieceInTheWayExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(playerCountExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(roleUnfulfilledExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(tooManyMovesExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(tooManyPiecesExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(tooManyTilesExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(uniquePlayersExResponse.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void handleNotFoundExceptions() {
        ClaimNotFoundException claimNotFoundEx = new ClaimNotFoundException("");
        NotFoundException notFoundEx = new NotFoundException(UUID.randomUUID());
        StartingPieceNotFoundException startingPieceNotFoundEx = new StartingPieceNotFoundException(1);

        ResponseEntity<String> claimNotFoundExResponse = controller.handleNotFoundErrors(claimNotFoundEx);
        ResponseEntity<String> notFoundExResponse = controller.handleNotFoundErrors(notFoundEx);
        ResponseEntity<String> startingPieceNotFoundExResponse = controller.handleNotFoundErrors(startingPieceNotFoundEx);

        assertThat(claimNotFoundExResponse.getStatusCode().value()).isEqualTo(404);
        assertThat(notFoundExResponse.getStatusCode().value()).isEqualTo(404);
        assertThat(startingPieceNotFoundExResponse.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void handleIllegalArgumentExceptions() {
        IllegalArgumentException illegalEx = new IllegalArgumentException();
        ResponseEntity<String> response = controller.handleIllegalArgumentException(illegalEx);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void handleIllegalStateExceptions() {
        IllegalStateException illegalStateEx = new IllegalStateException();
        ResponseEntity<String> response = controller.handleIllegalStateException(illegalStateEx);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void handleBotExceptions() {
        BotMoveRequestFailedException botMoveRequestFailedEx = new BotMoveRequestFailedException("", "");
        BotServiceNotReachableException botServiceNotReachableEx = new BotServiceNotReachableException("");

        ResponseEntity<String> botMoveRequestFailedExResponse = controller.handleBotExceptions(botMoveRequestFailedEx);
        ResponseEntity<String> botServiceNotReachableExResponse = controller.handleBotExceptions(botServiceNotReachableEx);

        assertThat(botMoveRequestFailedExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(botServiceNotReachableExResponse.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void handlePlayerAuthExceptions() {
        PlayerIdentityMismatchException playerIdentityMismatchEx = new PlayerIdentityMismatchException();
        PlayerNotInThisGameException playerNotInThisGameEx = new PlayerNotInThisGameException();

        ResponseEntity<String> playerIdentityMismatchExResponse = controller.handlePlayerAuthExceptions(playerIdentityMismatchEx);
        ResponseEntity<String> playerNotInThisGameExResponse = controller.handlePlayerAuthExceptions(playerNotInThisGameEx);

        assertThat(playerIdentityMismatchExResponse.getStatusCode().value()).isEqualTo(403);
        assertThat(playerNotInThisGameExResponse.getStatusCode().value()).isEqualTo(403);
    }
}