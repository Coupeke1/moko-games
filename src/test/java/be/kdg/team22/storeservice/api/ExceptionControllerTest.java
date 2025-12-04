package be.kdg.team22.storeservice.api;

import be.kdg.team22.storeservice.domain.cart.exceptions.CartNotFoundException;
import be.kdg.team22.storeservice.domain.cart.exceptions.GameAlreadyInCartException;
import be.kdg.team22.storeservice.domain.cart.exceptions.GameAlreadyOwnedException;
import be.kdg.team22.storeservice.domain.cart.exceptions.InvalidMetadataException;
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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExceptionControllerTest {

    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ThrowingController())
                .setControllerAdvice(new ExceptionController())
                .build();
    }

    @Test
    @DisplayName("NOT_FOUND → 404")
    void notFoundHandler() throws Exception {
        mockMvc.perform(get("/test/ex/notfound"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("No cart exists for user: ")));
    }

    @Test
    @DisplayName("BAD_REQUEST → GameAlreadyInCartException → 400")
    void badRequestHandler1() throws Exception {
        mockMvc.perform(get("/test/ex/badrequest1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("is already in cart for user")));
    }

    @Test
    @DisplayName("BAD_REQUEST → GameAlreadyOwnedException → 400")
    void badRequestHandler2() throws Exception {
        mockMvc.perform(get("/test/ex/badrequest2"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("already owns game")));
    }

    @Test
    @DisplayName("BAD_REQUEST → InvalidMetadataException → 400")
    void invalidMetadataHandler() throws Exception {
        mockMvc.perform(get("/test/ex/invalid-metadata"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid metadata")));
    }

    @Test
    @DisplayName("SERVICE_UNAVAILABLE → 503")
    void unavailableHandler() throws Exception {
        mockMvc.perform(get("/test/ex/unavailable"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string(containsString("Game-Service is currently unavailable")));
    }

    @Test
    @DisplayName("Unhandled exception → 500")
    void genericExceptionHandler() throws Exception {
        mockMvc.perform(get("/test/ex/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Internal server error")));
    }

    @RestController
    @RequestMapping("/test/ex")
    static class ThrowingController {

        @GetMapping("/notfound")
        public void notFound() {
            throw new CartNotFoundException(UUID.randomUUID());
        }

        @GetMapping("/badrequest1")
        public void badRequest1() {
            throw new GameAlreadyInCartException(UUID.randomUUID(), UUID.randomUUID());
        }

        @GetMapping("/badrequest2")
        public void badRequest2() {
            throw new GameAlreadyOwnedException(UUID.randomUUID(), UUID.randomUUID());
        }

        @GetMapping("/invalid-metadata")
        public void invalidMetadata() {
            throw new InvalidMetadataException(UUID.randomUUID(), "Missing title");
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