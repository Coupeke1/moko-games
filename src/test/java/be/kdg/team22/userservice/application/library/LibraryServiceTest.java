package be.kdg.team22.userservice.application.library;

import be.kdg.team22.userservice.api.library.models.LibraryGamesModel;
import be.kdg.team22.userservice.domain.library.GameId;
import be.kdg.team22.userservice.domain.library.LibraryEntry;
import be.kdg.team22.userservice.domain.library.LibraryId;
import be.kdg.team22.userservice.domain.library.LibraryRepository;
import be.kdg.team22.userservice.domain.library.exceptions.LibraryException;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class LibraryServiceTest {
    private final LibraryRepository repo = mock(LibraryRepository.class);
    private final ExternalGamesRepository games = mock(ExternalGamesRepository.class);

    private final LibraryService service = new LibraryService(repo, games);

    private Jwt token() {
        return Jwt.withTokenValue("dummy").header("alg", "none").subject("11111111-1111-1111-1111-111111111111").build();
    }

    private GameDetailsResponse game(UUID id, String title) {
        return new GameDetailsResponse(id, "EngineName", title, "Fun game", BigDecimal.TEN, "img.png");
    }

    private LibraryEntry entry(UUID id, UUID userId, UUID gameId, boolean favourite) {
        return new LibraryEntry(new LibraryId(id), new ProfileId(userId), new GameId(gameId), Instant.parse("2024-01-01T10:00:00Z"), favourite);
    }

    @Test
    @DisplayName("Maps all entries to LibraryGameModel")
    void getLibraryForUser_mapsEntries() {
        ProfileId userId = new ProfileId(UUID.randomUUID());
        UUID game1 = UUID.randomUUID();
        UUID game2 = UUID.randomUUID();

        LibraryEntry e1 = entry(UUID.randomUUID(), userId.value(), game1, true);
        LibraryEntry e2 = entry(UUID.randomUUID(), userId.value(), game2, false);

        when(repo.findByUserId(userId.value())).thenReturn(List.of(e1, e2));
        when(games.getGame(eq(game1), any())).thenReturn(game(game1, "A"));
        when(games.getGame(eq(game2), any())).thenReturn(game(game2, "B"));

        LibraryGamesModel result = service.getLibraryForUser(userId, token(), null, null, "title_asc", 100);

        assertThat(result.games()).hasSize(2);
        assertThat(result.games().get(0).id()).isEqualTo(game1);
        assertThat(result.games().get(0).favourite()).isTrue();
        assertThat(result.games().get(1).id()).isEqualTo(game2);
        assertThat(result.games().get(1).favourite()).isFalse();

        verify(repo).findByUserId(userId.value());
        verify(games, times(2)).getGame(any(), any());
    }

    @Test
    @DisplayName("Filter by title or description")
    void getLibraryForUser_filter() {
        ProfileId userId = new ProfileId(UUID.randomUUID());
        UUID game1 = UUID.randomUUID();
        UUID game2 = UUID.randomUUID();

        LibraryEntry e1 = entry(UUID.randomUUID(), userId.value(), game1, false);
        LibraryEntry e2 = entry(UUID.randomUUID(), userId.value(), game2, false);

        when(repo.findByUserId(userId.value())).thenReturn(List.of(e1, e2));

        when(games.getGame(eq(game1), any())).thenReturn(game(game1, "Chess"));
        when(games.getGame(eq(game2), any())).thenReturn(game(game2, "Monopoly"));

        LibraryGamesModel result = service.getLibraryForUser(userId, token(), "ch", null, "title_asc", 100);

        assertThat(result.games()).hasSize(1);
        assertThat(result.games().getFirst().title()).isEqualTo("Chess");
    }

    @Test
    @DisplayName("Filter favourite only")
    void getLibraryForUser_filterFavourite() {
        ProfileId userId = new ProfileId(UUID.randomUUID());
        UUID g1 = UUID.randomUUID();
        UUID g2 = UUID.randomUUID();

        LibraryEntry e1 = entry(UUID.randomUUID(), userId.value(), g1, true);
        LibraryEntry e2 = entry(UUID.randomUUID(), userId.value(), g2, false);

        when(repo.findByUserId(userId.value())).thenReturn(List.of(e1, e2));

        when(games.getGame(eq(g1), any())).thenReturn(game(g1, "A"));
        when(games.getGame(eq(g2), any())).thenReturn(game(g2, "B"));

        LibraryGamesModel result = service.getLibraryForUser(userId, token(), null, true, "title_asc", 100);

        assertThat(result.games()).hasSize(1);
        assertThat(result.games().getFirst().id()).isEqualTo(g1);
        assertThat(result.games().getFirst().favourite()).isTrue();
    }

    @Test
    @DisplayName("Ordering by title_desc")
    void getLibraryForUser_ordering() {
        ProfileId userId = new ProfileId(UUID.randomUUID());
        UUID g1 = UUID.randomUUID();
        UUID g2 = UUID.randomUUID();

        LibraryEntry e1 = entry(UUID.randomUUID(), userId.value(), g1, false);
        LibraryEntry e2 = entry(UUID.randomUUID(), userId.value(), g2, false);

        when(repo.findByUserId(userId.value())).thenReturn(List.of(e1, e2));

        when(games.getGame(eq(g1), any())).thenReturn(game(g1, "Alpha"));
        when(games.getGame(eq(g2), any())).thenReturn(game(g2, "Zulu"));

        LibraryGamesModel result = service.getLibraryForUser(userId, token(), null, null, "title_desc", 100);

        assertThat(result.games()).hasSize(2);
        assertThat(result.games().getFirst().title()).isEqualTo("Zulu");
        assertThat(result.games().get(1).title()).isEqualTo("Alpha");
    }

    @Test
    @DisplayName("Limit reduces result size")
    void getLibraryForUser_limit() {
        ProfileId userId = new ProfileId(UUID.randomUUID());
        UUID g1 = UUID.randomUUID();
        UUID g2 = UUID.randomUUID();

        LibraryEntry e1 = entry(UUID.randomUUID(), userId.value(), g1, false);
        LibraryEntry e2 = entry(UUID.randomUUID(), userId.value(), g2, false);

        when(repo.findByUserId(userId.value())).thenReturn(List.of(e1, e2));

        when(games.getGame(eq(g1), any())).thenReturn(game(g1, "Alpha"));
        when(games.getGame(eq(g2), any())).thenReturn(game(g2, "Zulu"));

        LibraryGamesModel result = service.getLibraryForUser(userId, token(), null, null, "title_asc", 1);

        assertThat(result.games()).hasSize(1);
    }

    @Test
    @DisplayName("Empty library returns empty model")
    void getLibraryForUser_empty() {
        ProfileId userId = new ProfileId(UUID.randomUUID());

        when(repo.findByUserId(userId.value())).thenReturn(List.of());

        LibraryGamesModel result = service.getLibraryForUser(userId, token(), null, null, "title_asc", 100);

        assertThat(result.games()).isEmpty();
        verifyNoInteractions(games);
    }

    @Test
    @DisplayName("markFavourite → marks entry as favourite and saves it")
    void markFavourite_success() {
        ProfileId userId = new ProfileId(UUID.randomUUID());
        UUID gameId = UUID.randomUUID();
        UUID entryId = UUID.randomUUID();

        LibraryEntry entry = entry(entryId, userId.value(), gameId, false);
        LibraryEntry updated = entry.markFavourite();

        when(repo.findByUserIdAndGameId(userId.value(), gameId)).thenReturn(java.util.Optional.of(entry));

        service.markFavourite(userId, new GameId(gameId));

        verify(repo).findByUserIdAndGameId(userId.value(), gameId);
        verify(repo).save(updated);
    }

    @Test
    @DisplayName("markFavourite → throws when entry does not exist")
    void markFavourite_notFound() {
        ProfileId userId = new ProfileId(UUID.randomUUID());
        UUID gameId = UUID.randomUUID();

        when(repo.findByUserIdAndGameId(userId.value(), gameId)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> service.markFavourite(userId, new GameId(gameId))).isInstanceOf(LibraryException.class);

        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("unmarkFavourite → unmarks entry and saves it")
    void unmarkFavourite_success() {
        ProfileId userId = new ProfileId(UUID.randomUUID());
        UUID gameId = UUID.randomUUID();
        UUID entryId = UUID.randomUUID();

        LibraryEntry entry = entry(entryId, userId.value(), gameId, true);
        LibraryEntry updated = entry.unmarkFavourite();

        when(repo.findByUserIdAndGameId(userId.value(), gameId)).thenReturn(java.util.Optional.of(entry));

        service.unmarkFavourite(userId, new GameId(gameId));

        verify(repo).findByUserIdAndGameId(userId.value(), gameId);
        verify(repo).save(updated);
    }

    @Test
    @DisplayName("unmarkFavourite → throws when entry does not exist")
    void unmarkFavourite_notFound() {
        ProfileId userId = new ProfileId(UUID.randomUUID());
        UUID gameId = UUID.randomUUID();

        when(repo.findByUserIdAndGameId(userId.value(), gameId)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> service.unmarkFavourite(userId, new GameId(gameId))).isInstanceOf(LibraryException.class);

        verify(repo, never()).save(any());
    }
}