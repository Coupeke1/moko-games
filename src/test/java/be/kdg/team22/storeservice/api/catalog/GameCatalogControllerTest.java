package be.kdg.team22.storeservice.api.catalog;

import be.kdg.team22.storeservice.api.catalog.models.GameCatalogResponse;
import be.kdg.team22.storeservice.application.catalog.queries.FilterQuery;
import be.kdg.team22.storeservice.application.catalog.queries.Pagination;
import be.kdg.team22.storeservice.application.catalog.services.GameQueryService;
import be.kdg.team22.storeservice.application.catalog.services.StoreService;
import be.kdg.team22.storeservice.config.TestSecurityConfig;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;
import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import be.kdg.team22.storeservice.infrastructure.games.GameMetadataResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameCatalogController.class)
@Import(TestSecurityConfig.class)
class GameCatalogControllerTest {

    private final UUID GAME_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private StoreService storeService;
    @MockitoBean
    private GameQueryService queryService;

    private GameCatalogEntry sampleEntry() {
        return new GameCatalogEntry(
                GAME_ID,
                BigDecimal.valueOf(10),
                GameCategory.STRATEGY,
                5
        );
    }

    private GameMetadataResponse sampleMetadata() {
        return new GameMetadataResponse(
                GAME_ID,
                "engineName",
                "Chess",
                "Classic strategy game",
                "img.png",
                Instant.now(),
                Instant.now()
        );
    }

    private GameCatalogResponse sampleResponse() {
        return GameCatalogResponse.from(sampleEntry(), sampleMetadata());
    }

    @Test
    @DisplayName("GET /api/store/games returns paged list")
    void list_returnsPagedResponse() throws Exception {
        when(queryService.listGamesWithMetadata(any(), any()))
                .thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/store/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(GAME_ID.toString()))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.last").value(true));

        ArgumentCaptor<FilterQuery> filterCap = ArgumentCaptor.forClass(FilterQuery.class);
        ArgumentCaptor<Pagination> pageCap = ArgumentCaptor.forClass(Pagination.class);

        verify(queryService).listGamesWithMetadata(filterCap.capture(), pageCap.capture());
        assertThat(pageCap.getValue().page()).isEqualTo(0);
        assertThat(pageCap.getValue().size()).isEqualTo(10);
    }

    @Test
    @DisplayName("GET /api/store/games?sort=alphabetic sorts alphabetically")
    void list_sortsAlphabetically() throws Exception {
        GameCatalogResponse a = new GameCatalogResponse(GAME_ID, "Alpha", "d", "i", BigDecimal.TEN, GameCategory.CARD, 5, 0);
        GameCatalogResponse b = new GameCatalogResponse(GAME_ID, "Zulu", "d", "i", BigDecimal.TEN, GameCategory.CARD, 5, 0);

        when(queryService.listGamesWithMetadata(any(), any()))
                .thenReturn(List.of(b, a)); // reversed order

        mockMvc.perform(get("/api/store/games?sort=alphabetic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].title").value("Alpha"))
                .andExpect(jsonPath("$.items[1].title").value("Zulu"));
    }

    @Test
    @DisplayName("GET /api/store/games/{id} returns single catalog entry")
    void get_returnsGame() throws Exception {
        when(queryService.getGameWithMetadata(GAME_ID))
                .thenReturn(sampleResponse());

        mockMvc.perform(get("/api/store/games/{id}", GAME_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(GAME_ID.toString()))
                .andExpect(jsonPath("$.title").value("Chess"))
                .andExpect(jsonPath("$.price").value(10.0));
    }

    @Test
    @DisplayName("POST /api/store/games creates entry and returns metadata response")
    void create_createsEntry() throws Exception {
        GameCatalogEntry entry = sampleEntry();

        when(storeService.create(any(), any(), any()))
                .thenReturn(entry);

        when(queryService.getGameWithMetadata(entry.getId()))
                .thenReturn(sampleResponse());

        mockMvc.perform(post("/api/store/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "00000000-0000-0000-0000-000000000001",
                                  "price": 10,
                                  "category": "STRATEGY"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(GAME_ID.toString()))
                .andExpect(jsonPath("$.title").value("Chess"));

        verify(storeService).create(eq(GAME_ID), eq(BigDecimal.valueOf(10)), eq(GameCategory.STRATEGY));
    }

    @Test
    @DisplayName("POST /api/store/games returns 400 when validation fails")
    void create_validationFails_returns400() throws Exception {
        GameCatalogEntry entry = sampleEntry();

        when(storeService.create(any(), any(), any()))
                .thenReturn(entry);
        mockMvc.perform(post("/api/store/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "price": -5,
                                  "category": "STRATEGY"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/store/games/{id} updates entry")
    void update_updatesEntry() throws Exception {
        GameCatalogEntry entry = sampleEntry();
        when(storeService.update(eq(GAME_ID), any(), any()))
                .thenReturn(entry);
        when(queryService.getGameWithMetadata(GAME_ID))
                .thenReturn(sampleResponse());

        mockMvc.perform(put("/api/store/games/{id}", GAME_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "price": 15,
                                  "category": "CARD"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(10.0));
    }

    @Test
    @DisplayName("PUT /api/store/games/{id} returns 400 when validation fails")
    void update_validationFails_returns400() throws Exception {

        mockMvc.perform(put("/api/store/games/{id}", GAME_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "price": -1,
                                  "category": "STRATEGY"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/store/games/{id} removes entry")
    void delete_removesEntry() throws Exception {
        doNothing().when(storeService).delete(GAME_ID);

        mockMvc.perform(delete("/api/store/games/{id}", GAME_ID))
                .andExpect(status().isNoContent());

        verify(storeService).delete(GAME_ID);
    }
}