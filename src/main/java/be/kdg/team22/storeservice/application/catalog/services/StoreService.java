package be.kdg.team22.storeservice.application.catalog.services;

import be.kdg.team22.storeservice.application.catalog.queries.FilterQuery;
import be.kdg.team22.storeservice.application.catalog.queries.Pagination;
import be.kdg.team22.storeservice.domain.catalog.*;
import be.kdg.team22.storeservice.domain.catalog.exceptions.GameNotFoundException;
import be.kdg.team22.storeservice.infrastructure.games.ExternalGamesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

@Service
@Transactional
public class StoreService {
    private final EntryRepository repository;
    private final ExternalGamesRepository games;

    public StoreService(final EntryRepository repository, final ExternalGamesRepository games) {
        this.repository = repository;
        this.games = games;
    }

    public Entry get(final GameId id) {
        return repository.findById(id.value()).orElseThrow(id::notFound);
    }

    public Collection<Post> getPosts(final GameId id) {
        final Entry entry = repository.findByIdWithPosts(id.value()).orElseThrow(id::notFound);
        return entry.posts();
    }

    public Post getPost(final GameId entryId, final PostId postId) {
        final Entry entry = repository.findByIdWithPosts(entryId.value()).orElseThrow(entryId::notFound);
        return entry.findPost(postId).orElseThrow(postId::notFound);
    }

    public Collection<Entry> list(final FilterQuery filter, final Pagination pagination) {
        return repository.findAll(filter, pagination);
    }

    public Entry create(final GameId id, final BigDecimal price, final GameCategory category) {
        games.fetchMetadata(id);
        final Entry entry = new Entry(id, price, category, Collections.emptyList());

        repository.save(entry);
        return entry;
    }

    public Entry update(final GameId id, final BigDecimal price, final GameCategory category) {
        final Entry existing = repository.findById(id.value()).orElseThrow(id::notFound);
        existing.updatePriceAndCategory(price, category);

        repository.save(existing);
        return existing;
    }

    public void recordPurchase(final GameId gameId) {
        final Entry entry = repository.findById(gameId.value()).orElseThrow(() -> new GameNotFoundException(gameId));
        entry.recordPurchase();
        repository.save(entry);
    }

    public void delete(final GameId id) {
        repository.delete(id.value());
    }
}