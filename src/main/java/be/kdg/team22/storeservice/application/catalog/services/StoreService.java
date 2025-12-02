package be.kdg.team22.storeservice.application.catalog.services;

import be.kdg.team22.storeservice.application.catalog.queries.FilterQuery;
import be.kdg.team22.storeservice.application.catalog.queries.Pagination;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogRepository;
import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import be.kdg.team22.storeservice.domain.catalog.exceptions.GameNotFoundException;
import be.kdg.team22.storeservice.infrastructure.games.ExternalGamesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class StoreService {
    private final GameCatalogRepository repo;
    private final ExternalGamesRepository games;

    public StoreService(GameCatalogRepository repo, ExternalGamesRepository games) {
        this.repo = repo;
        this.games = games;
    }

    public GameCatalogEntry get(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));
    }

    public List<GameCatalogEntry> list(FilterQuery filter, Pagination pagination) {
        return repo.findAll(filter, pagination);
    }

    public GameCatalogEntry create(UUID id, BigDecimal price, GameCategory category) {
        games.fetchMetadata(id);

        GameCatalogEntry entry = new GameCatalogEntry(id, price, category);

        repo.save(entry);
        return entry;
    }

    public GameCatalogEntry update(UUID id, BigDecimal price, GameCategory category) {
        GameCatalogEntry existing = repo.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));

        existing.updatePriceAndCategory(price, category);

        repo.save(existing);
        return existing;
    }

    public void recordPurchase(UUID gameId) {
        GameCatalogEntry entry = repo.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        entry.recordPurchase();

        repo.save(entry);
    }

    public void delete(UUID id) {
        repo.delete(id);
    }
}