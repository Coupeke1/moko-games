package be.kdg.team22.storeservice.application.catalog.services;


import be.kdg.team22.storeservice.application.catalog.queries.FilterQuery;
import be.kdg.team22.storeservice.application.catalog.queries.Pagination;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogRepository;
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

    public GameCatalogEntry create(GameCatalogEntry entry) {

        GameMetadataResponse metadata = games.fetchMetadata(entry.getId());

        entry.update(metadata.title(), entry.getPrice(), entry.getCategory());

        repo.save(entry);
        return entry;
    }
}