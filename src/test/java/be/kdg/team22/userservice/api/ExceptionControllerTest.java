package be.kdg.team22.userservice.api;

import be.kdg.team22.userservice.domain.library.exceptions.ExternalGameNotFoundException;
import be.kdg.team22.userservice.domain.library.exceptions.GameServiceNotReachableException;
import be.kdg.team22.userservice.domain.library.exceptions.LibraryException;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.domain.profile.exceptions.ClaimNotFoundException;
import be.kdg.team22.userservice.domain.profile.exceptions.NotAuthenticatedException;
import be.kdg.team22.userservice.domain.profile.exceptions.ProfileNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionControllerTest {

    private final ExceptionController controller = new ExceptionController();

    @Test
    @DisplayName("ClaimNotFoundException → 404")
    void handleNotFound_claim() {
        RuntimeException ex = new ClaimNotFoundException("claim missing");

        ResponseEntity<String> response = controller.handleNotFound(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isEqualTo("Claim 'claim missing' was not found");
    }

    @Test
    @DisplayName("ProfileNotFoundException → 404")
    void handleNotFound_profile() {
        RuntimeException ex = new ProfileNotFoundException(ProfileId.from(UUID.randomUUID()));

        ResponseEntity<String> response = controller.handleNotFound(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).contains("Profile with id ");
    }

    @Test
    @DisplayName("NotAuthenticatedException → 403")
    void handleNotAuthenticated() {
        NotAuthenticatedException ex = new NotAuthenticatedException();

        ResponseEntity<String> response = controller.handleNotAuthenticated(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(response.getBody()).isEqualTo("User is not authenticated");
    }

    @Test
    @DisplayName("LibraryException.missingGameId → 400 BAD_REQUEST")
    void handleMissingGameId() {
        ResponseEntity<String> response = controller.handleBadRequest(LibraryException.missingGameId());

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo("gameId cannot be null");
    }

    @Test
    @DisplayName("LibraryException.missingUserId → 400 BAD_REQUEST")
    void handleMissingUserId() {
        ResponseEntity<String> response = controller.handleBadRequest(LibraryException.missingUserId());

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo("userId cannot be null");
    }

    @Test
    @DisplayName("LibraryException.missingPurchasedAt → 400 BAD_REQUEST")
    void handleMissingPurchasedAt() {
        ResponseEntity<String> response = controller.handleBadRequest(LibraryException.missingPurchasedAt());

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo("purchasedAt cannot be null");
    }

    @Test
    @DisplayName("Unknown exception → 500 INTERNAL_SERVER_ERROR")
    void handleUnknown() {
        Exception ex = new Exception("kaboom");

        ResponseEntity<String> response = controller.handleUnknown(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isEqualTo("Internal server error: kaboom");
    }

    @Test
    @DisplayName("GameServiceNotReachableException → 503 SERVICE_UNAVAILABLE")
    void handleGameServiceNotReachable() {
        GameServiceNotReachableException ex = new GameServiceNotReachableException("http://games");

        ResponseEntity<String> response = controller.handleServiceUnavailable(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(503);
        assertThat(response.getBody()).isEqualTo("Game-service is not reachable at 'http://games'");
    }
    
    @Test
    @DisplayName("ExternalGameNotFoundException → 404")
    void handleNotFound_externalGame() {
        UUID gameId = UUID.randomUUID();
        RuntimeException ex = new ExternalGameNotFoundException(gameId);

        ResponseEntity<String> response = controller.handleNotFound(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isEqualTo(
                String.format("External game with id '%s' was not found", gameId)
        );
    }
}