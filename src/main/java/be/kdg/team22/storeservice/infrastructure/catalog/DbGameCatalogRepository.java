package be.kdg.team22.storeservice.infrastructure.catalog;

import be.kdg.team22.storeservice.application.catalog.queries.FilterQuery;
import be.kdg.team22.storeservice.application.catalog.queries.Pagination;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;
import be.kdg.team22.storeservice.domain.catalog.GameCatalogRepository;
import be.kdg.team22.storeservice.infrastructure.catalog.jpa.GameCatalogEntryEntity;
import be.kdg.team22.storeservice.infrastructure.catalog.jpa.JpaGameCatalogEntryRepository;
import be.kdg.team22.storeservice.infrastructure.util.SortingUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class DbGameCatalogRepository implements GameCatalogRepository {

    private final JpaGameCatalogEntryRepository jpa;

    public DbGameCatalogRepository(JpaGameCatalogEntryRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<GameCatalogEntry> findById(UUID id) {
        return jpa.findById(id)
                .map(GameCatalogEntryEntity::toDomain);
    }

    @Override
    public List<GameCatalogEntry> findAll(FilterQuery filter, Pagination pagination) {
        return jpa.findAll()
                .stream()
                .map(GameCatalogEntryEntity::toDomain)
                .filter(e -> filter.category.map(c -> e.getCategory() == c).orElse(true))
                .filter(e -> filter.minPrice.map(min -> e.getPrice().compareTo(min) >= 0).orElse(true))
                .filter(e -> filter.maxPrice.map(max -> e.getPrice().compareTo(max) <= 0).orElse(true))
                .sorted((a, b) -> SortingUtils.sort(a, b, filter.sortBy))
                .skip((long) pagination.page() * pagination.size())
                .limit(pagination.size())
                .toList();
    }

    @Override
    public void save(GameCatalogEntry entry) {
        jpa.save(GameCatalogEntryEntity.fromDomain(entry));
    }

    @Override
    public void delete(UUID id) {
        jpa.deleteById(id);
    }
}