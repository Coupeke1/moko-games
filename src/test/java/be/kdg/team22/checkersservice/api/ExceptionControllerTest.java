package be.kdg.team22.checkersservice.api;

import be.kdg.team22.checkersservice.domain.game.exceptions.GameIdException;
import be.kdg.team22.checkersservice.domain.game.exceptions.NotFoundException;
import be.kdg.team22.checkersservice.domain.game.exceptions.PlayerCountException;
import be.kdg.team22.checkersservice.domain.game.exceptions.UniquePlayersException;
import be.kdg.team22.checkersservice.domain.player.exceptions.InvalidPlayerException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ExceptionControllerTest {

    private final ExceptionController controller = new ExceptionController();

    @Test
    void handleDomainExceptions() {
        GameIdException gameIdEx = new GameIdException();
        PlayerCountException playerCountEx = new PlayerCountException();
        UniquePlayersException uniquePlayersEx = new UniquePlayersException();
        InvalidPlayerException invalidPlayerEx = new InvalidPlayerException();

        ResponseEntity<String> gameIdExResponse = controller.handleDomainErrors(gameIdEx);
        ResponseEntity<String> playerCountExResponse = controller.handleDomainErrors(playerCountEx);
        ResponseEntity<String> uniquePlayersExResponse = controller.handleDomainErrors(uniquePlayersEx);
        ResponseEntity<String> invalidPlayerExResponse = controller.handleDomainErrors(invalidPlayerEx);

        assertThat(gameIdExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(playerCountExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(uniquePlayersExResponse.getStatusCode().value()).isEqualTo(400);
        assertThat(invalidPlayerExResponse.getStatusCode().value()).isEqualTo(400);
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