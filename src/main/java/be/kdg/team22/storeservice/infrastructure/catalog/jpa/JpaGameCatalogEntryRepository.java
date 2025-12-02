package be.kdg.team22.storeservice.infrastructure.catalog.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaGameCatalogEntryRepository extends JpaRepository<GameCatalogEntryEntity, UUID> {
}
