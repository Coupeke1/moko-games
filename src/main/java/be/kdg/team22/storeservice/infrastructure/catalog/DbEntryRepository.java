package be.kdg.team22.storeservice.infrastructure.catalog;

import be.kdg.team22.storeservice.application.catalog.queries.FilterQuery;
import be.kdg.team22.storeservice.application.catalog.queries.Pagination;
import be.kdg.team22.storeservice.domain.catalog.Entry;
import be.kdg.team22.storeservice.domain.catalog.EntryRepository;
import be.kdg.team22.storeservice.infrastructure.catalog.jpa.EntryEntity;
import be.kdg.team22.storeservice.infrastructure.catalog.jpa.JpaEntryRepository;
import be.kdg.team22.storeservice.infrastructure.util.SortingUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class DbEntryRepository implements EntryRepository {

    private final JpaEntryRepository jpa;

    public DbEntryRepository(JpaEntryRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Entry> findById(UUID id) {
        return jpa.findById(id)
                .map(EntryEntity::to);
    }

    @Override
    public Optional<Entry> findByIdWithPosts(UUID id) {
        return jpa.findById(id).map(EntryEntity::toWithPosts);
    }

    @Override
    public List<Entry> findAll(FilterQuery filter, Pagination pagination) {
        return jpa.findAll()
                .stream()
                .map(EntryEntity::to)
                .filter(e -> filter.category.map(c -> e.category() == c).orElse(true))
                .filter(e -> filter.minPrice.map(min -> e.price().compareTo(min) >= 0).orElse(true))
                .filter(e -> filter.maxPrice.map(max -> e.price().compareTo(max) <= 0).orElse(true))
                .sorted((a, b) -> SortingUtils.sort(a, b, filter.sortBy))
                .skip((long) pagination.page() * pagination.size())
                .limit(pagination.size())
                .toList();
    }

    @Override
    public void save(Entry entry) {
        jpa.save(EntryEntity.from(entry));
    }

    @Override
    public void delete(UUID id) {
        jpa.deleteById(id);
    }
}