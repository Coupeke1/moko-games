package be.kdg.team22.storeservice.application.catalog.services;

import be.kdg.team22.storeservice.api.catalog.models.GameCatalogResponse;
import be.kdg.team22.storeservice.application.catalog.queries.FilterQuery;
import be.kdg.team22.storeservice.application.catalog.queries.Pagination;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogRepository;
import be.kdg.team22.storeservice.domain.catalog.exceptions.GameNotFoundException;
import be.kdg.team22.storeservice.infrastructure.games.ExternalGamesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GameQueryService {
    private final GameCatalogRepository repo;
    private final ExternalGamesRepository games;

    public GameQueryService(GameCatalogRepository repo, ExternalGamesRepository games) {
        this.repo = repo;
        this.games = games;
    }

    public GameCatalogResponse getGameWithMetadata(UUID id) {
        GameCatalogEntry entry = repo.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));

        var metadata = games.fetchMetadata(id);
        return GameCatalogResponse.from(entry, metadata);
    }

    public List<GameCatalogResponse> listGamesWithMetadata(FilterQuery filter, Pagination pagination) {
        List<GameCatalogEntry> entries = repo.findAll(filter, pagination);

        return entries.stream()
                .map(entry -> {
                    var metadata = games.fetchMetadata(entry.getId());
                    return GameCatalogResponse.from(entry, metadata);
                })
                .toList();
    }
}