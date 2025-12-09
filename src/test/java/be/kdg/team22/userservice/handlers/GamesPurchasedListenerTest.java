package be.kdg.team22.userservice.handlers;

import be.kdg.team22.userservice.application.library.LibraryService;
import be.kdg.team22.userservice.domain.library.GameId;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.events.GamesPurchasedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class GamesPurchasedListenerTest {

    private final LibraryService libraryService = mock(LibraryService.class);
    private final GamesPurchasedListener listener = new GamesPurchasedListener(libraryService);

    @Test
    @DisplayName("handleGamesPurchased → adds single game to library")
    void handleGamesPurchased_singleGame() {
        UUID userId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        GamesPurchasedEvent event = new GamesPurchasedEvent(userId, List.of(gameId));

        listener.handleGamesPurchased(event);

        verify(libraryService).addGameToLibrary(ProfileId.from(userId), GameId.from(gameId));
    }

    @Test
    @DisplayName("handleGamesPurchased → adds multiple games to library")
    void handleGamesPurchased_multipleGames() {
        UUID userId = UUID.randomUUID();
        UUID gameId1 = UUID.randomUUID();
        UUID gameId2 = UUID.randomUUID();
        UUID gameId3 = UUID.randomUUID();
        GamesPurchasedEvent event = new GamesPurchasedEvent(userId, List.of(gameId1, gameId2, gameId3));

        listener.handleGamesPurchased(event);

        verify(libraryService).addGameToLibrary(ProfileId.from(userId), GameId.from(gameId1));
        verify(libraryService).addGameToLibrary(ProfileId.from(userId), GameId.from(gameId2));
        verify(libraryService).addGameToLibrary(ProfileId.from(userId), GameId.from(gameId3));
        verify(libraryService, times(3)).addGameToLibrary(any(), any());
    }

    @Test
    @DisplayName("handleGamesPurchased → empty list does not call service")
    void handleGamesPurchased_emptyList() {
        UUID userId = UUID.randomUUID();
        GamesPurchasedEvent event = new GamesPurchasedEvent(userId, List.of());

        listener.handleGamesPurchased(event);

        verifyNoInteractions(libraryService);
    }

    @Test
    @DisplayName("handleGamesPurchased → continues processing when one game fails")
    void handleGamesPurchased_continuesOnError() {
        UUID userId = UUID.randomUUID();
        UUID gameId1 = UUID.randomUUID();
        UUID gameId2 = UUID.randomUUID();
        UUID gameId3 = UUID.randomUUID();
        GamesPurchasedEvent event = new GamesPurchasedEvent(userId, List.of(gameId1, gameId2, gameId3));

        doThrow(new RuntimeException("Database error"))
                .when(libraryService).addGameToLibrary(ProfileId.from(userId), GameId.from(gameId2));

        listener.handleGamesPurchased(event);

        verify(libraryService).addGameToLibrary(ProfileId.from(userId), GameId.from(gameId1));
        verify(libraryService).addGameToLibrary(ProfileId.from(userId), GameId.from(gameId2));
        verify(libraryService).addGameToLibrary(ProfileId.from(userId), GameId.from(gameId3));
    }

    @Test
    @DisplayName("handleGamesPurchased → processes remaining games after exception")
    void handleGamesPurchased_allGamesAttempted() {
        UUID userId = UUID.randomUUID();
        UUID gameId1 = UUID.randomUUID();
        UUID gameId2 = UUID.randomUUID();
        GamesPurchasedEvent event = new GamesPurchasedEvent(userId, List.of(gameId1, gameId2));

        doThrow(new RuntimeException("First game fails"))
                .when(libraryService).addGameToLibrary(ProfileId.from(userId), GameId.from(gameId1));

        listener.handleGamesPurchased(event);

        // Second game should still be attempted
        verify(libraryService).addGameToLibrary(ProfileId.from(userId), GameId.from(gameId2));
    }
}