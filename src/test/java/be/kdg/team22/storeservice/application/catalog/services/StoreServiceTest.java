package be.kdg.team22.storeservice.application.catalog.services;

import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogRepository;
import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import be.kdg.team22.storeservice.domain.catalog.exceptions.GameNotFoundException;
import be.kdg.team22.storeservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.storeservice.infrastructure.games.GameMetadataResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceSociableTest {

    private final UUID GAME_ID = UUID.randomUUID();
    StoreService service;
    @Mock
    GameCatalogRepository repo;
    @Mock
    ExternalGamesRepository games;

    private GameMetadataResponse metadata() {
        return new GameMetadataResponse(
                GAME_ID,
                "engine",
                "Title",
                "Desc",
                "img.png",
                Instant.now(),
                Instant.now()
        );
    }

    @BeforeEach
    void setup() {
        service = new StoreService(repo, games);
    }

    @Test
    void create_validInput_createsEntryAndStoresIt() {
        when(games.fetchMetadata(GAME_ID)).thenReturn(metadata());

        GameCatalogEntry created = service.create(GAME_ID, BigDecimal.TEN, GameCategory.CARD);

        assertThat(created.getId()).isEqualTo(GAME_ID);
        assertThat(created.getPrice()).isEqualTo(BigDecimal.TEN);
        assertThat(created.getCategory()).isEqualTo(GameCategory.CARD);
        assertThat(created.getPurchaseCount()).isZero();
        assertThat(created.getPopularityScore()).isZero();

        verify(repo).save(created);
    }

    @Test
    void create_failsWhenGameDoesNotExist() {
        when(games.fetchMetadata(GAME_ID)).thenThrow(new GameNotFoundException(GAME_ID));

        assertThatThrownBy(() ->
                service.create(GAME_ID, BigDecimal.TEN, GameCategory.STRATEGY)
        ).isInstanceOf(GameNotFoundException.class);

        verify(repo, never()).save(any());
    }

    @Test
    void update_updatesAndPersists() {
        GameCatalogEntry entry = new GameCatalogEntry(
                GAME_ID, BigDecimal.ONE, GameCategory.FAMILY, 3
        );

        when(repo.findById(GAME_ID)).thenReturn(Optional.of(entry));
        when(repo.findById(GAME_ID)).thenReturn(Optional.of(entry));

        GameCatalogEntry result =
                service.update(GAME_ID, BigDecimal.valueOf(20), GameCategory.CARD);

        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(20));
        assertThat(result.getCategory()).isEqualTo(GameCategory.CARD);

        verify(repo).save(entry);
    }

    @Test
    void update_whenNotFound_throwsException() {
        when(repo.findById(GAME_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.update(GAME_ID, BigDecimal.ONE, GameCategory.PARTY)
        ).isInstanceOf(GameNotFoundException.class);
    }

    @Test
    void delete_callsRepoDelete() {
        service.delete(GAME_ID);
        verify(repo).delete(GAME_ID);
    }
}