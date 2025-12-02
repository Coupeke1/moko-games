package be.kdg.team22.storeservice.application.catalog.services;

import be.kdg.team22.storeservice.application.catalog.queries.FilterQuery;
import be.kdg.team22.storeservice.application.catalog.queries.Pagination;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogRepository;
import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import be.kdg.team22.storeservice.domain.catalog.GameWithMetadata;
import be.kdg.team22.storeservice.domain.catalog.exceptions.GameNotFoundException;
import be.kdg.team22.storeservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.storeservice.infrastructure.games.GameMetadataResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StoreService {

    private final GameCatalogRepository repo;
    private final ExternalGamesRepository games;

    public StoreService(GameCatalogRepository repo, ExternalGamesRepository games) {
        this.repo = repo;
        this.games = games;
    }

    public GameWithMetadata get(UUID id) {
        GameCatalogEntry entry = repo.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));

        GameMetadataResponse meta = games.fetchMetadata(id);

        return new GameWithMetadata(entry, meta);
    }

    public List<GameWithMetadata> list(FilterQuery filter, Pagination pagination) {
        List<GameCatalogEntry> entries = repo.findAll(filter, pagination);
        return entries.stream()
                .map(entry -> new GameWithMetadata(entry, games.fetchMetadata(entry.getId())))
                .toList();
    }

    public GameCatalogEntry create(UUID id,
                                   double price,
                                   GameCategory category,
                                   Double popularity) {
        games.fetchMetadata(id);

        GameCatalogEntry entry = new GameCatalogEntry(id, price, category,
                popularity != null ? popularity : 0);

        repo.save(entry);
        return entry;
    }

    public GameCatalogEntry update(UUID id,
                                   double price,
                                   GameCategory category) {

        GameCatalogEntry existing = repo.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));

        existing.update(price, category);

        repo.save(existing);
        return existing;
    }

    public void delete(UUID id) {
        repo.delete(id);
    }
}