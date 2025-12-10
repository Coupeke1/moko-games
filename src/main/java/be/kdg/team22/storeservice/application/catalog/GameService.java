package be.kdg.team22.storeservice.application.catalog;

import be.kdg.team22.storeservice.api.catalog.models.EntryModel;
import be.kdg.team22.storeservice.application.catalog.queries.FilterQuery;
import be.kdg.team22.storeservice.application.catalog.queries.Pagination;
import be.kdg.team22.storeservice.domain.catalog.Entry;
import be.kdg.team22.storeservice.domain.catalog.EntryRepository;
import be.kdg.team22.storeservice.domain.catalog.GameId;
import be.kdg.team22.storeservice.domain.catalog.exceptions.GameNotFoundException;
import be.kdg.team22.storeservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.storeservice.infrastructure.games.GameMetadataResponse;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class GameService {
    private final EntryRepository repo;
    private final ExternalGamesRepository games;

    public GameService(final EntryRepository repo, final ExternalGamesRepository games) {
        this.repo = repo;
        this.games = games;
    }

    public List<EntryModel> listGamesWithMetadata(final FilterQuery filter, final Pagination pagination) {
        List<Entry> entries = repo.findAll(filter, pagination);

        List<EntryModel> games = entries.stream().map(entry -> EntryModel.from(entry, this.games.fetchMetadata(entry.id()))).toList();

        if (filter.query.isPresent() && !filter.query.get().isBlank()) {
            String query = filter.query.get();

            games = games.stream().filter(game -> (game.title() != null && game.title().toLowerCase().contains(query.toLowerCase())) || (game.description() != null && game.description().toLowerCase().contains(query))).toList();
        }

        if (filter.sortBy.isPresent() && filter.sortBy.get().equals("alphabetic")) {
            games = games.stream().sorted(Comparator.comparing(EntryModel::title)).toList();
        }

        return games;
    }

    public EntryModel getGameWithMetadata(final GameId id) {
        Entry entry = repo.findById(id.value()).orElseThrow(() -> new GameNotFoundException(id));
        GameMetadataResponse meta = games.fetchMetadata(id);
        return EntryModel.from(entry, meta);
    }
}
