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

    public StoreService(final GameCatalogRepository repo, final ExternalGamesRepository games) {
        this.repo = repo;
        this.games = games;
    }

    public GameCatalogEntry get(final UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));
    }

    public List<GameCatalogEntry> list(final FilterQuery filter, final Pagination pagination) {
        return repo.findAll(filter, pagination);
    }

    public GameCatalogEntry create(final UUID id, final BigDecimal price, final GameCategory category) {
        games.fetchMetadata(id);

        GameCatalogEntry entry = new GameCatalogEntry(id, price, category);

        repo.save(entry);
        return entry;
    }

    public GameCatalogEntry update(final UUID id, final BigDecimal price, final GameCategory category) {
        GameCatalogEntry existing = repo.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));

        existing.updatePriceAndCategory(price, category);

        repo.save(existing);
        return existing;
    }

    public void recordPurchase(final UUID gameId) {
        GameCatalogEntry entry = repo.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));
        entry.recordPurchase();
        repo.save(entry);
    }

    public void delete(final UUID id) {
        repo.delete(id);
    }
}