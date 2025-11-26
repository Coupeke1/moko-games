package be.kdg.team22.userservice.application.library;

import be.kdg.team22.userservice.api.library.models.LibraryGamesModel;
import be.kdg.team22.userservice.domain.library.LibraryEntry;
import be.kdg.team22.userservice.domain.library.LibraryRepository;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.userservice.infrastructure.games.GameDetailsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LibraryServiceTest {

    private final LibraryRepository repo = mock(LibraryRepository.class);
    private final ExternalGamesRepository games = mock(ExternalGamesRepository.class);

    private final LibraryService service = new LibraryService(repo, games);

    private Jwt token() {
        return Jwt.withTokenValue("dummy")
                .header("alg", "none")
                .subject("11111111-1111-1111-1111-111111111111")
                .build();
    }

    private GameDetailsResponse game(UUID id) {
        return new GameDetailsResponse(
                id,
                "EngineName",
                "Tic Tac Toe",
                "Fun game",
                BigDecimal.TEN,
                "img.png",
                "https://store.example.com"
        );
    }

    @Test
    @DisplayName("getLibraryForUser → maps all entries to LibraryGameModel")
    void getLibraryForUser_success() {

        ProfileId userId = new ProfileId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        Instant purchasedAt = Instant.parse("2024-01-01T10:00:00Z");

        LibraryEntry entry1 = new LibraryEntry(
                UUID.randomUUID(),
                userId.value(),
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                purchasedAt
        );

        LibraryEntry entry2 = new LibraryEntry(
                UUID.randomUUID(),
                userId.value(),
                UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                purchasedAt
        );

        when(repo.findByUserId(userId.value())).thenReturn(List.of(entry1, entry2));

        when(games.getGame(eq(entry1.gameId()), any(Jwt.class)))
                .thenReturn(game(entry1.gameId()));

        when(games.getGame(eq(entry2.gameId()), any(Jwt.class)))
                .thenReturn(game(entry2.gameId()));

        LibraryGamesModel result = service.getLibraryForUser(userId, token());

        assertThat(result.games()).hasSize(2);
        assertThat(result.games().get(0).id()).isEqualTo(entry1.gameId());
        assertThat(result.games().get(1).id()).isEqualTo(entry2.gameId());

        verify(repo).findByUserId(userId.value());
        verify(games, times(2)).getGame(any(), any());
    }

    @Test
    @DisplayName("getLibraryForUser → empty library returns empty model")
    void getLibraryForUser_emptyLibrary() {

        ProfileId userId = new ProfileId(UUID.randomUUID());

        when(repo.findByUserId(userId.value())).thenReturn(List.of());

        LibraryGamesModel result = service.getLibraryForUser(userId, token());

        assertThat(result.games()).isEmpty();

        verify(repo).findByUserId(userId.value());
        verifyNoInteractions(games);
    }
}