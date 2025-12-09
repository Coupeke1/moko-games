package be.kdg.team22.storeservice.api.catalog;

import be.kdg.team22.storeservice.api.catalog.models.*;
import be.kdg.team22.storeservice.application.catalog.queries.FilterQuery;
import be.kdg.team22.storeservice.application.catalog.queries.Pagination;
import be.kdg.team22.storeservice.application.catalog.GameService;
import be.kdg.team22.storeservice.application.catalog.StoreService;
import be.kdg.team22.storeservice.domain.catalog.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/store/games")
public class CatalogController {
    private final StoreService storeService;
    private final GameService queryService;

    public CatalogController(final StoreService storeService, final GameService queryService) {
        this.storeService = storeService;
        this.queryService = queryService;
    }

    @GetMapping
    public ResponseEntity<PagedResponse<EntryModel>> list(@RequestParam(required = false) final String query, @RequestParam(required = false) final GameCategory category, @RequestParam(required = false) final BigDecimal minPrice, @RequestParam(required = false) final BigDecimal maxPrice, @RequestParam(required = false) final String sort, @RequestParam(defaultValue = "0") final int page, @RequestParam(defaultValue = "10") final int size) {
        FilterQuery filter = new FilterQuery();
        filter.query = Optional.ofNullable(query);
        filter.category = Optional.ofNullable(category);
        filter.minPrice = Optional.ofNullable(minPrice);
        filter.maxPrice = Optional.ofNullable(maxPrice);
        filter.sortBy = Optional.ofNullable(sort);

        Pagination pagination = new Pagination(page, size);
        List<EntryModel> games = queryService.listGamesWithMetadata(filter, pagination);

        boolean last = games.size() < size;
        return ResponseEntity.ok(new PagedResponse<>(games, page, size, last));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntryModel> get(@PathVariable final UUID id) {
        EntryModel model = queryService.getGameWithMetadata(GameId.from(id));
        return ResponseEntity.ok(model);
    }

    @GetMapping("/{id}/posts")
    public ResponseEntity<Collection<PostModel>> getPosts(@PathVariable final UUID id) {
        List<PostModel> models = storeService.getPosts(GameId.from(id)).stream().map(PostModel::from).toList();
        return ResponseEntity.ok(models);
    }

    @GetMapping("/{entryId}/posts/{postId}")
    public ResponseEntity<PostModel> getPost(@PathVariable final UUID entryId, @PathVariable final UUID postId) {
        Post post = storeService.getPost(GameId.from(entryId), PostId.from(postId));
        return ResponseEntity.ok(PostModel.from(post));
    }

    @PostMapping
    public ResponseEntity<EntryModel> create(@Valid @RequestBody final NewEntryModel request) {
        Entry entry = storeService.create(GameId.from(request.id()), request.price(), request.category());
        EntryModel response = queryService.getGameWithMetadata(entry.id());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntryModel> update(@PathVariable final UUID id, @Valid @RequestBody final UpdateEntryModel request) {
        Entry entry = storeService.update(GameId.from(id), request.price(), request.category());
        return ResponseEntity.ok(queryService.getGameWithMetadata(entry.id()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final UUID id) {
        storeService.delete(GameId.from(id));
        return ResponseEntity.noContent().build();
    }
}