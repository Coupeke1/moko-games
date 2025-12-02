package be.kdg.team22.storeservice.domain.catalog;

import be.kdg.team22.storeservice.application.catalog.queries.FilterQuery;
import be.kdg.team22.storeservice.application.catalog.queries.Pagination;
import org.jmolecules.ddd.annotation.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameCatalogRepository {

    Optional<GameCatalogEntry> findById(UUID id);

    List<GameCatalogEntry> findAll(FilterQuery filter, Pagination pagination);

    void save(GameCatalogEntry entry);

    void delete(UUID id);
}
