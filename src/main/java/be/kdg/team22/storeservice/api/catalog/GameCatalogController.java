package be.kdg.team22.storeservice.api.catalog;

import be.kdg.team22.storeservice.api.catalog.models.GameCatalogRequestModel;
import be.kdg.team22.storeservice.api.catalog.models.GameCatalogResponse;
import be.kdg.team22.storeservice.api.catalog.models.PagedResponse;
import be.kdg.team22.storeservice.application.catalog.queries.FilterQuery;
import be.kdg.team22.storeservice.application.catalog.queries.Pagination;
import be.kdg.team22.storeservice.application.catalog.services.StoreService;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;
import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        filter.category = category != null ? java.util.Optional.of(category) : java.util.Optional.empty();
        filter.minPrice = minPrice != null ? java.util.Optional.of(minPrice) : java.util.Optional.empty();
        filter.maxPrice = maxPrice != null ? java.util.Optional.of(maxPrice) : java.util.Optional.empty();
        filter.sortBy = sort != null ? java.util.Optional.of(sort) : java.util.Optional.empty();

        Pagination pagination = new Pagination(page, size);

        List<GameCatalogEntry> entries = service.list(filter, pagination);

        List<GameCatalogResponse> dto = entries.stream()
                .map(GameCatalogResponse::from)
                .toList();

        boolean last = dto.size() < size;

        return new PagedResponse<>(dto, page, size, last);
    }

    @GetMapping("/{id}")
    public GameCatalogResponse get(@PathVariable UUID id) {
        return GameCatalogResponse.from(service.get(id));
    }

    @PostMapping
    public GameCatalogResponse create(@RequestBody GameCatalogRequestModel request) {
        var entry = service.create(
                request.id(),
                request.price(),
                request.category(),
                request.popularity()
        );
        return GameCatalogResponse.from(entry);
    }

    @PutMapping("/{id}")
    public GameCatalogResponse update(
            @PathVariable UUID id,
            @RequestBody GameCatalogRequestModel request
    ) {
        var entry = service.update(
                id,
                request.price(),
                request.category()
        );
        return GameCatalogResponse.from(entry);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}