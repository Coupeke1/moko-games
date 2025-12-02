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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Comparator;
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
    public PagedResponse<GameCatalogResponse> list(
            @RequestParam(required = false) GameCategory category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        FilterQuery filter = new FilterQuery();
        filter.category = Optional.ofNullable(category);
        filter.minPrice = Optional.ofNullable(minPrice);
        filter.maxPrice = Optional.ofNullable(maxPrice);
        filter.sortBy = Optional.ofNullable(sort);

        Pagination pagination = new Pagination(page, size);

        List<GameCatalogResponse> games = queryService.listGamesWithMetadata(filter, pagination);

        if ("alphabetic".equals(sort)) {
            games = games.stream()
                    .sorted(Comparator.comparing(GameCatalogResponse::title))
                    .toList();
        }

        boolean last = games.size() < size;
        return new PagedResponse<>(games, page, size, last);
    }

    @GetMapping("/{id}")
    public GameCatalogResponse get(@PathVariable UUID id) {
        return queryService.getGameWithMetadata(id);
    }

    @PostMapping
    public GameCatalogResponse create(@RequestBody GameCatalogRequestModel request) {
        GameCatalogEntry entry = storeService.create(
                request.id(),
                request.price(),
                request.category()
        );

        return queryService.getGameWithMetadata(entry.getId());
    }

    @PutMapping("/{id}")
    public GameCatalogResponse update(
            @PathVariable UUID id,
            @RequestBody UpdateGameCatalogModel request
    ) {
        GameCatalogEntry entry = storeService.update(
                id,
                request.price(),
                request.category()
        );

        return queryService.getGameWithMetadata(entry.getId());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        storeService.delete(id);
    }
}