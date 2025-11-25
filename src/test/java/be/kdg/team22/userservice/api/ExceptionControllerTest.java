package be.kdg.team22.userservice.api;

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

        assertThat(response.getStatusCode().value()).isEqualTo(403);
        assertThat(response.getBody()).isEqualTo("User is not authenticated");
    }

    @Test
    @DisplayName("IllegalArgumentException → 400 BAD_REQUEST")
    void handleBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("invalid input");

        ResponseEntity<String> response = controller.handleBadRequest(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo("invalid input");
    }

    @Test
    @DisplayName("Unknown exception → 500 INTERNAL_SERVER_ERROR")
    void handleUnknown() {
        Exception ex = new Exception("kaboom");

        ResponseEntity<String> response = controller.handleUnknown(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isEqualTo("Internal server error: kaboom");
    }
}