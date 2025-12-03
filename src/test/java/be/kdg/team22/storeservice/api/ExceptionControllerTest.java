package be.kdg.team22.storeservice.api;

import be.kdg.team22.storeservice.domain.cart.exceptions.CartNotFoundException;
import be.kdg.team22.storeservice.domain.cart.exceptions.GameAlreadyInCartException;
import be.kdg.team22.storeservice.domain.exceptions.ServiceUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the ExceptionController via a dummy controller that throws the exceptions.
 */
class ExceptionControllerTest {

    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ThrowingController())
                .setControllerAdvice(new ExceptionController())
                .build();
    }

    @Test
    @DisplayName("NOT_FOUND exceptions → 404")
    void notFoundHandler() throws Exception {
        mockMvc.perform(get("/test/ex/notfound"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("No cart exists for user:")));
    }

    @Test
    @DisplayName("BAD_REQUEST exceptions → 400")
    void badRequestHandler() throws Exception {
        mockMvc.perform(get("/test/ex/badrequest"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("SERVICE_UNAVAILABLE exceptions → 503")
    void unavailableHandler() throws Exception {
        mockMvc.perform(get("/test/ex/unavailable"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    @DisplayName("Unhandled exceptions → 500")
    void genericExceptionHandler() throws Exception {
        mockMvc.perform(get("/test/ex/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Internal server error")));
    }

    @RestController
    @RequestMapping("/test/ex")
    static class ThrowingController {

        @GetMapping("/notfound")
        public void notFound() {
            throw new CartNotFoundException(UUID.randomUUID());
        }

        @GetMapping("/badrequest")
        public void badRequest() {
            throw new GameAlreadyInCartException(UUID.randomUUID(), UUID.randomUUID());
        }

        @GetMapping("/unavailable")
        public void unavailable() {
            throw ServiceUnavailableException.GameServiceUnavailable();
        }

        @GetMapping("/generic")
        public void generic() {
            throw new RuntimeException("boom");
        }
    }
}