package be.kdg.team22.storeservice.application.catalog.services;

import be.kdg.team22.storeservice.api.catalog.models.EntryModel;
import be.kdg.team22.storeservice.application.catalog.GameService;
import be.kdg.team22.storeservice.application.catalog.queries.FilterQuery;
import be.kdg.team22.storeservice.application.catalog.queries.Pagination;
import be.kdg.team22.storeservice.domain.catalog.Entry;
import be.kdg.team22.storeservice.domain.catalog.EntryRepository;
import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import be.kdg.team22.storeservice.domain.catalog.GameId;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceSociableTest {

    private final UUID ID1 = UUID.randomUUID();
    private final UUID ID2 = UUID.randomUUID();
    GameService queryService;
    @Mock
    EntryRepository repo;
    @Mock
    ExternalGamesRepository games;

    private Entry entry1() {
        return new Entry(GameId.from(ID1), BigDecimal.valueOf(10), GameCategory.CARD, 5, Collections.emptyList());
    }

    private Entry entry2() {
        return new Entry(GameId.from(ID2), BigDecimal.valueOf(20), GameCategory.STRATEGY, 3, Collections.emptyList());
    }

    private GameMetadataResponse meta(UUID id, String title) {
        return new GameMetadataResponse(id, "engine", title, "desc", "img.png", Instant.now(), Instant.now());
    }

    @BeforeEach
    void setup() {
        queryService = new GameService(repo, games);
    }

    @Test
    void list_returnsMappedResponses() {
        when(repo.findAll(any(), any())).thenReturn(List.of(entry1(), entry2()));

        when(games.fetchMetadata(GameId.from(ID1))).thenReturn(meta(ID1, "A-Title"));
        when(games.fetchMetadata(GameId.from(ID2))).thenReturn(meta(ID2, "B-Title"));

        List<EntryModel> responses = queryService.listGamesWithMetadata(new FilterQuery(), new Pagination(0, 10));

        assertThat(responses).extracting(EntryModel::title).containsExactly("A-Title", "B-Title");
    }

    @Test
    void list_alphabeticSorting_appliesCorrectly() {
        when(repo.findAll(any(), any())).thenReturn(List.of(entry1(), entry2()));

        when(games.fetchMetadata(GameId.from(ID1))).thenReturn(meta(ID1, "Zulu"));
        when(games.fetchMetadata(GameId.from(ID2))).thenReturn(meta(ID2, "Alpha"));

        FilterQuery filter = new FilterQuery();
        filter.sortBy = Optional.of("alphabetic");

        List<EntryModel> responses = queryService.listGamesWithMetadata(filter, new Pagination(0, 10));

        assertThat(responses.get(0).title()).isEqualTo("Alpha");
        assertThat(responses.get(1).title()).isEqualTo("Zulu");
    }

    @Test
    void get_returnsMappedResponse() {
        when(repo.findById(ID1)).thenReturn(Optional.of(entry1()));
        when(games.fetchMetadata(GameId.from(ID1))).thenReturn(meta(ID1, "Chess"));

        EntryModel response = queryService.getGameWithMetadata(GameId.from(ID1));

        assertThat(response.title()).isEqualTo("Chess");
        assertThat(response.id()).isEqualTo(ID1);
    }

    @Test
    void get_whenNotFound_throwsException() {
        when(repo.findById(ID1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> queryService.getGameWithMetadata(GameId.from(ID1))).isInstanceOf(RuntimeException.class);
    }
}