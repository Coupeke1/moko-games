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

import static org.mockito.ArgumentMatchers.*;
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

    private UsernamePasswordAuthenticationToken authWithUser(UUID id) {
        Jwt jwt = Jwt.withTokenValue("token-" + id)
                .header("alg", "none")
                .subject(id.toString())
                .claim("preferred_username", "mathias")
                .build();

        return new UsernamePasswordAuthenticationToken(jwt, jwt.getTokenValue(), List.of());
    }

    private Jwt extractJwt(UsernamePasswordAuthenticationToken token) {
        return (Jwt) token.getPrincipal();
    }

    @Test
    @DisplayName("GET /api/library/me → returns library for authenticated user")
    void getMyLibrary_success() throws Exception {
        UUID userId = UUID.randomUUID();
        ProfileId profileId = ProfileId.from(userId);
        UsernamePasswordAuthenticationToken auth = authWithUser(userId);
        Jwt jwt = extractJwt(auth);

        LibraryGameModel gameModel = new LibraryGameModel(
                UUID.randomUUID(),
                "Tic Tac Toe",
                "desc",
                BigDecimal.TEN,
                "img.png",
                "url",
                Instant.parse("2024-01-01T10:00:00Z"),
                true
        );

        LibraryGamesModel response = new LibraryGamesModel(List.of(gameModel));

        when(libraryService.getLibraryForUser(
                eq(profileId),
                eq(jwt),
                isNull(),
                isNull(),
                eq("title_asc"),
                eq(100)
        )).thenReturn(response);

        mockMvc.perform(get("/api/library/me").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Tic Tac Toe"));

        verify(libraryService).getLibraryForUser(
                eq(profileId),
                eq(jwt),
                isNull(),
                isNull(),
                eq("title_asc"),
                eq(100)
        );
    }

    @Test
    @DisplayName("GET /api/library/me with filters → service receives correct parameters")
    void getMyLibrary_withFilters() throws Exception {
        UUID userId = UUID.randomUUID();
        ProfileId profileId = ProfileId.from(userId);
        UsernamePasswordAuthenticationToken auth = authWithUser(userId);
        Jwt jwt = extractJwt(auth);

        LibraryGamesModel response = new LibraryGamesModel(List.of());
        when(libraryService.getLibraryForUser(any(), any(), any(), any(), any(), any()))
                .thenReturn(response);

        mockMvc.perform(get("/api/library/me")
                        .param("filter", "ch")
                        .param("favourite", "true")
                        .param("order", "purchased_desc")
                        .param("limit", "5")
                        .with(authentication(auth)))
                .andExpect(status().isOk());

        verify(libraryService).getLibraryForUser(
                eq(profileId),
                eq(jwt),
                eq("ch"),
                eq(true),
                eq("purchased_desc"),
                eq(5)
        );
    }

    @Test
    @DisplayName("GET /api/library/me → empty list")
    void getMyLibrary_empty() throws Exception {
        UUID userId = UUID.randomUUID();
        ProfileId profileId = ProfileId.from(userId);
        UsernamePasswordAuthenticationToken auth = authWithUser(userId);
        Jwt jwt = extractJwt(auth);

        when(libraryService.getLibraryForUser(eq(profileId), eq(jwt), any(), any(), any(), any()))
                .thenReturn(new LibraryGamesModel(List.of()));

        mockMvc.perform(get("/api/library/me").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/library/me → 401 when no JWT")
    void getMyLibrary_noJwtUnauthorized() throws Exception {
        mockMvc.perform(get("/api/library/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/library/me → 400 when subject is not a valid UUID")
    void getMyLibrary_invalidUuid() throws Exception {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("not-a-uuid")
                .claim("preferred_username", "mathias")
                .build();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(jwt, jwt.getTokenValue(), List.of());

        mockMvc.perform(get("/api/library/me").with(authentication(auth)))
                .andExpect(status().isBadRequest());
    }
}