package be.kdg.team22.userservice.api.library;

import be.kdg.team22.userservice.api.library.models.LibraryGameModel;
import be.kdg.team22.userservice.api.library.models.LibraryGamesModel;
import be.kdg.team22.userservice.application.library.LibraryService;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LibraryController.class)
class LibraryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    LibraryService libraryService;

    private UsernamePasswordAuthenticationToken jwtAuth(String uuid) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(uuid)
                .claim("preferred_username", "mathias")
                .build();

        return new UsernamePasswordAuthenticationToken(jwt, jwt.getTokenValue(), List.of());
    }

    @Test
    @DisplayName("GET /api/library/me → returns library for authenticated user")
    void getMyLibrary_success() throws Exception {
        UUID userId = UUID.randomUUID();
        ProfileId profileId = ProfileId.from(userId);
        LibraryGameModel gameModel = new LibraryGameModel(
                UUID.randomUUID(),
                "Tic Tac Toe",
                "desc",
                BigDecimal.valueOf(20),
                "img.png",
                "url",
                Instant.parse("2024-01-01T10:00:00Z")
        );

        LibraryGamesModel response = new LibraryGamesModel(List.of(gameModel));

        when(libraryService.getLibraryForUser(eq(profileId), eq((Jwt) jwtAuth(userId.toString()).getPrincipal())))
                .thenReturn(response);

        mockMvc.perform(get("/api/library/me")
                        .with(authentication(jwtAuth(userId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.games.length()").value(1))
                .andExpect(jsonPath("$.games[0].title").value("Tic Tac Toe"));

        verify(libraryService).getLibraryForUser(eq(profileId), eq((Jwt) jwtAuth(userId.toString()).getPrincipal()));
    }

    @Test
    @DisplayName("GET /api/library/me → returns empty list when user has no games")
    void getMyLibrary_empty() throws Exception {
        UUID userId = UUID.randomUUID();
        ProfileId profileId = ProfileId.from(userId);

        LibraryGamesModel empty = new LibraryGamesModel(List.of());

        when(libraryService.getLibraryForUser(eq(profileId), eq((Jwt) jwtAuth(userId.toString()).getPrincipal())))
                .thenReturn(empty);

        mockMvc.perform(get("/api/library/me")
                        .with(authentication(jwtAuth(userId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.games.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/library/me → 401 without JWT")
    void getMyLibrary_noJwtUnauthorized() throws Exception {
        mockMvc.perform(get("/api/library/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/library/me → subject is not a UUID → 400 BAD REQUEST")
    void getMyLibrary_invalidUuid() throws Exception {
        UsernamePasswordAuthenticationToken auth = jwtAuth("not-a-uuid");

        mockMvc.perform(get("/api/library/me").with(authentication(auth)))
                .andExpect(status().isBadRequest());
    }
}