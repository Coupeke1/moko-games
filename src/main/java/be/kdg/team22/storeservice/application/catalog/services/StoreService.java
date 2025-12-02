package be.kdg.team22.storeservice.application.catalog.services;

import be.kdg.team22.storeservice.application.catalog.queries.FilterQuery;
import be.kdg.team22.storeservice.application.catalog.queries.Pagination;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogRepository;
import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import be.kdg.team22.storeservice.domain.catalog.exceptions.GameNotFoundException;
import be.kdg.team22.storeservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.storeservice.infrastructure.games.GameMetadataResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StoreService {

    private final GameCatalogRepository catalogRepository;
    private final ExternalGamesRepository gamesRepository;

    public StoreService(GameCatalogRepository catalogRepository, ExternalGamesRepository gamesRepository) {
        this.catalogRepository = catalogRepository;
        this.gamesRepository = gamesRepository;
    }

    public List<GameCatalogEntry> list(FilterQuery filter, Pagination pagination) {
        return catalogRepository.findAll(filter, pagination);
    }

    public GameCatalogEntry get(UUID id) {
        return catalogRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));
    }

    public GameCatalogEntry create(UUID id,
                                   double price,
                                   GameCategory category,
                                   Double initialPopularity) {

        GameMetadataResponse metadata = gamesRepository.fetchMetadata(id);

        double popularity = initialPopularity != null ? initialPopularity : 0.0;

        GameCatalogEntry entry = new GameCatalogEntry(
                id,
                metadata.title(),
                price,
                category,
                popularity
        );

        catalogRepository.save(entry);
        return entry;
    }

    public GameCatalogEntry update(UUID id,
                                   double price,
                                   GameCategory category) {
        GameCatalogEntry existing = get(id);
        existing.update(existing.getTitle(), price, category);
        catalogRepository.save(existing);
        return existing;
    }

    public void delete(UUID id) {
        catalogRepository.delete(id);
    }
}