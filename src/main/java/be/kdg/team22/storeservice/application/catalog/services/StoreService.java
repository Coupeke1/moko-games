package be.kdg.team22.storeservice.application.catalog.services;

import be.kdg.team22.storeservice.application.catalog.queries.FilterQuery;
import be.kdg.team22.storeservice.application.catalog.queries.Pagination;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogRepository;
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

    public List<GameCatalogEntry> list(FilterQuery filter, Pagination pagination) {
        return repo.findAll(filter, pagination);
    }

    public GameCatalogEntry get(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));
    }

    public GameCatalogEntry create(UUID id,
                                   double price,
                                   be.kdg.team22.storeservice.domain.catalog.GameCategory category,
                                   Double initialPopularity) {

        GameMetadataResponse metadata = games.fetchMetadata(id);

        double popularity = initialPopularity != null ? initialPopularity : 0.0;

        GameCatalogEntry entry = new GameCatalogEntry(
                id,
                metadata.title(),
                price,
                category,
                popularity
        );

        repo.save(entry);
        return entry;
    }

    public GameCatalogEntry update(UUID id,
                                   double price,
                                   be.kdg.team22.storeservice.domain.catalog.GameCategory category) {
        GameCatalogEntry existing = get(id);
        existing.update(existing.getTitle(), price, category);
        repo.save(existing);
        return existing;
    }

    public void delete(UUID id) {
        repo.delete(id);
    }
}