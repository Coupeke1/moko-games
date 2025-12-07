package be.kdg.team22.storeservice.application.catalog.services;

import be.kdg.team22.storeservice.api.catalog.models.GameCatalogResponse;
import be.kdg.team22.storeservice.application.catalog.queries.FilterQuery;
import be.kdg.team22.storeservice.application.catalog.queries.Pagination;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogRepository;
import be.kdg.team22.storeservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.storeservice.infrastructure.games.GameMetadataResponse;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class GameQueryService {
    private final GameCatalogRepository repo;
    private final ExternalGamesRepository games;

    public GameQueryService(final GameCatalogRepository repo, final ExternalGamesRepository games) {
        this.repo = repo;
        this.games = games;
    }

    public List<GameCatalogResponse> listGamesWithMetadata(final FilterQuery filter, final Pagination pagination) {

        List<GameCatalogEntry> entries = repo.findAll(filter, pagination);

        List<GameCatalogResponse> games = entries.stream().map(entry -> GameCatalogResponse.from(entry, this.games.fetchMetadata(entry.getId()))).toList();

        if (filter.query.isPresent() && !filter.query.get().isBlank()) {
            String query = filter.query.get();

            games = games.stream().filter(game -> (game.title() != null && game.title().toLowerCase().contains(query.toLowerCase())) || (game.description() != null && game.description().toLowerCase().contains(query))).toList();
        }

        if (filter.sortBy.isPresent() && filter.sortBy.get().equals("alphabetic")) {
            games = games.stream().sorted(Comparator.comparing(GameCatalogResponse::title)).toList();
        }

        return games;
    }

    public GameCatalogResponse getGameWithMetadata(final UUID id) {

        GameCatalogEntry entry = repo.findById(id).orElseThrow();
        GameMetadataResponse meta = games.fetchMetadata(id);

        return GameCatalogResponse.from(entry, meta);
    }
}
