package be.kdg.team22.storeservice.api.catalog;

import be.kdg.team22.storeservice.api.catalog.models.GameCatalogRequestModel;
import be.kdg.team22.storeservice.api.catalog.models.GameCatalogResponse;
import be.kdg.team22.storeservice.api.catalog.models.PagedResponse;
import be.kdg.team22.storeservice.application.catalog.queries.FilterQuery;
import be.kdg.team22.storeservice.application.catalog.queries.Pagination;
import be.kdg.team22.storeservice.application.catalog.services.StoreService;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;
import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import be.kdg.team22.storeservice.domain.catalog.GameWithMetadata;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/store/games")
public class GameCatalogController {

    private final StoreService service;

    public GameCatalogController(StoreService service) {
        this.service = service;
    }

    @GetMapping
    public PagedResponse<GameCatalogResponse> list(
            @RequestParam(required = false) GameCategory category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
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

        List<GameWithMetadata> combined = service.list(filter, pagination);

        if ("alphabetic".equals(sort)) {
            combined = combined.stream()
                    .sorted(Comparator.comparing(g -> g.metadata().title()))
                    .toList();
        }

        List<GameCatalogResponse> dto = combined.stream()
                .map(gwm -> GameCatalogResponse.from(gwm.entry(), gwm.metadata()))
                .toList();

        boolean last = dto.size() < size;

        return new PagedResponse<>(dto, page, size, last);
    }

    @GetMapping("/{id}")
    public GameCatalogResponse get(@PathVariable UUID id) {
        GameWithMetadata gwm = service.get(id);
        return GameCatalogResponse.from(gwm.entry(), gwm.metadata());
    }

    @PostMapping
    public GameCatalogResponse create(@RequestBody GameCatalogRequestModel request) {
        GameCatalogEntry entry = service.create(
                request.id(),
                request.price(),
                request.category(),
                request.popularity()
        );

        GameWithMetadata game = service.get(entry.getId());
        return GameCatalogResponse.from(game.entry(), game.metadata());
    }

    @PutMapping("/{id}")
    public GameCatalogResponse update(
            @PathVariable UUID id,
            @RequestBody GameCatalogRequestModel request
    ) {
        GameCatalogEntry entry = service.update(
                id,
                request.price(),
                request.category()
        );

        GameWithMetadata game = service.get(entry.getId());
        return GameCatalogResponse.from(game.entry(), game.metadata());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}