package be.kdg.team22.storeservice.api.catalog;

import be.kdg.team22.storeservice.api.catalog.models.GameCatalogRequestModel;
import be.kdg.team22.storeservice.api.catalog.models.GameCatalogResponse;
import be.kdg.team22.storeservice.api.catalog.models.PagedResponse;
import be.kdg.team22.storeservice.api.catalog.models.UpdateGameCatalogModel;
import be.kdg.team22.storeservice.application.catalog.queries.FilterQuery;
import be.kdg.team22.storeservice.application.catalog.queries.Pagination;
import be.kdg.team22.storeservice.application.catalog.services.GameQueryService;
import be.kdg.team22.storeservice.application.catalog.services.StoreService;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;
import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/store/games")
public class GameCatalogController {
    private final StoreService storeService;
    private final GameQueryService queryService;

    public GameCatalogController(StoreService storeService, GameQueryService queryService) {
        this.storeService = storeService;
        this.queryService = queryService;
    }

    @GetMapping
    public ResponseEntity<PagedResponse<GameCatalogResponse>> list(@RequestParam(required = false) final String query, @RequestParam(required = false) final GameCategory category, @RequestParam(required = false) final BigDecimal minPrice, @RequestParam(required = false) final BigDecimal maxPrice, @RequestParam(required = false) final String sort, @RequestParam(defaultValue = "0") final int page, @RequestParam(defaultValue = "10") final int size) {
        FilterQuery filter = new FilterQuery();
        filter.query = Optional.ofNullable(query);
        filter.category = Optional.ofNullable(category);
        filter.minPrice = Optional.ofNullable(minPrice);
        filter.maxPrice = Optional.ofNullable(maxPrice);
        filter.sortBy = Optional.ofNullable(sort);

        Pagination pagination = new Pagination(page, size);
        List<GameCatalogResponse> games = queryService.listGamesWithMetadata(filter, pagination);

        boolean last = games.size() < size;
        return ResponseEntity.ok(new PagedResponse<>(games, page, size, last));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameCatalogResponse> get(@PathVariable final UUID id) {
        return ResponseEntity.ok(queryService.getGameWithMetadata(id));
    }

    @PostMapping
    public ResponseEntity<GameCatalogResponse> create(@Valid @RequestBody final GameCatalogRequestModel request) {
        GameCatalogEntry entry = storeService.create(request.id(), request.price(), request.category());
        return ResponseEntity.ok(queryService.getGameWithMetadata(entry.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameCatalogResponse> update(@PathVariable final UUID id, @Valid @RequestBody final UpdateGameCatalogModel request) {
        GameCatalogEntry entry = storeService.update(id, request.price(), request.category());
        return ResponseEntity.ok(queryService.getGameWithMetadata(entry.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final UUID id) {
        storeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}