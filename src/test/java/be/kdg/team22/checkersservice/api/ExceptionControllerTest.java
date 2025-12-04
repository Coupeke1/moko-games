package be.kdg.team22.checkersservice.api;

import be.kdg.team22.checkersservice.domain.move.exceptions.InvalidMoveException;
import be.kdg.team22.checkersservice.domain.game.exceptions.*;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;
import be.kdg.team22.checkersservice.domain.player.exceptions.InvalidPlayerException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ExceptionControllerTest {

    private final ExceptionController controller = new ExceptionController();

    @Test
    void handleDomainExceptions() {
        BoardSizeException boardSizeEx = new BoardSizeException();
        GameIdException gameIdEx = new GameIdException();
        GameNotRunningException gameNotRunningEx = new GameNotRunningException();
        InvalidMoveException invalidMoveEx = new InvalidMoveException("No Piece found");
        InvalidPlayerException invalidPlayerEx = new InvalidPlayerException();
        NotPlayersTurnException notPlayersTurnEx = new NotPlayersTurnException(PlayerRole.WHITE);
        OutsidePlayingFieldException outsidePlayingFieldEx = new OutsidePlayingFieldException();
        PlayerCountException playerCountEx = new PlayerCountException();
        RoleUnfulfilledException roleUnfulfilledEx = new RoleUnfulfilledException(PlayerRole.BLACK);
        UniquePlayersException uniquePlayersEx = new UniquePlayersException();

        ResponseEntity<String> boardSizeExResponse = controller.handleDomainErrors(boardSizeEx);
        ResponseEntity<String> gameIdExResponse = controller.handleDomainErrors(gameIdEx);
        ResponseEntity<String> gameNotRunningExResponse = controller.handleDomainErrors(gameNotRunningEx);
        ResponseEntity<String> invalidMoveExResponse = controller.handleDomainErrors(invalidMoveEx);
        ResponseEntity<String> invalidPlayerExResponse = controller.handleDomainErrors(invalidPlayerEx);
        ResponseEntity<String> notPlayersTurnExResponse = controller.handleDomainErrors(notPlayersTurnEx);
        ResponseEntity<String> outsidePlayingFieldExResponse = controller.handleDomainErrors(outsidePlayingFieldEx);
        ResponseEntity<String> playerCountExResponse = controller.handleDomainErrors(playerCountEx);
        ResponseEntity<String> roleUnfulfilledExceptionResponse = controller.handleDomainErrors(roleUnfulfilledEx);
        ResponseEntity<String> uniquePlayersExResponse = controller.handleDomainErrors(uniquePlayersEx);

        assertThat(boardSizeExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(gameIdExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(gameNotRunningExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(invalidMoveExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(invalidPlayerExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(notPlayersTurnExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(outsidePlayingFieldExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(playerCountExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(roleUnfulfilledExceptionResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(uniquePlayersExResponse.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void handleNotFoundExceptions() {
        NotFoundException notFoundEx = new NotFoundException(UUID.randomUUID());
        ResponseEntity<String> response = controller.handleNotFoundErrors(notFoundEx);
        assertThat(response.getStatusCode().value()).isEqualTo(404);
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
}