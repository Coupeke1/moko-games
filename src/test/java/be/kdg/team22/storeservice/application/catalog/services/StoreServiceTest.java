package be.kdg.team22.storeservice.application.catalog.services;

import be.kdg.team22.storeservice.application.catalog.StoreService;
import be.kdg.team22.storeservice.domain.catalog.Entry;
import be.kdg.team22.storeservice.domain.catalog.EntryRepository;
import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import be.kdg.team22.storeservice.domain.catalog.GameId;
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
import java.util.Collections;
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
    EntryRepository repo;
    @Mock
    ExternalGamesRepository games;

    private GameMetadataResponse metadata() {
        return new GameMetadataResponse(GAME_ID, "engine", "Title", "Desc", "img.png", Instant.now(), Instant.now());
    }

    @BeforeEach
    void setup() {
        service = new StoreService(repo, games);
    }

    @Test
    void create_validInput_createsEntryAndStoresIt() {
        when(games.fetchMetadata(GameId.from(GAME_ID))).thenReturn(metadata());

        Entry created = service.create(GameId.from(GAME_ID), BigDecimal.TEN, GameCategory.CARD);

        assertThat(created.id().value()).isEqualTo(GAME_ID);
        assertThat(created.price()).isEqualTo(BigDecimal.TEN);
        assertThat(created.category()).isEqualTo(GameCategory.CARD);
        assertThat(created.purchaseCount()).isZero();
        assertThat(created.popularity()).isZero();

        verify(repo).save(created);
    }

    @Test
    void create_failsWhenGameDoesNotExist() {
        GameId id = GameId.from(GAME_ID);
        when(games.fetchMetadata(id)).thenThrow(new GameNotFoundException(id));

        assertThatThrownBy(() -> service.create(GameId.from(GAME_ID), BigDecimal.TEN, GameCategory.STRATEGY)).isInstanceOf(GameNotFoundException.class);

        verify(repo, never()).save(any());
    }

    @Test
    void update_updatesAndPersists() {
        Entry entry = new Entry(GameId.from(GAME_ID), BigDecimal.ONE, GameCategory.FAMILY, 3, Collections.emptyList());

        when(repo.findById(GAME_ID)).thenReturn(Optional.of(entry));
        when(repo.findById(GAME_ID)).thenReturn(Optional.of(entry));

        Entry result = service.update(GameId.from(GAME_ID), BigDecimal.valueOf(20), GameCategory.CARD);

        assertThat(result.price()).isEqualTo(BigDecimal.valueOf(20));
        assertThat(result.category()).isEqualTo(GameCategory.CARD);

        verify(repo).save(entry);
    }

    @Test
    void update_whenNotFound_throwsException() {
        when(repo.findById(GAME_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(GameId.from(GAME_ID), BigDecimal.ONE, GameCategory.PARTY)).isInstanceOf(GameNotFoundException.class);
    }

    @Test
    void delete_callsRepoDelete() {
        service.delete(GameId.from(GAME_ID));
        verify(repo).delete(GAME_ID);
    }
}