package be.kdg.team22.storeservice.domain.catalog;

import be.kdg.team22.storeservice.application.catalog.queries.FilterQuery;
import be.kdg.team22.storeservice.application.catalog.queries.Pagination;
import org.jmolecules.ddd.annotation.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EntryRepository {

    Optional<Entry> findById(UUID id);

    Optional<Entry> findByIdWithPosts(UUID id);

    List<Entry> findAll(FilterQuery filter, Pagination pagination);

    void save(Entry entry);

    void delete(UUID id);
}
