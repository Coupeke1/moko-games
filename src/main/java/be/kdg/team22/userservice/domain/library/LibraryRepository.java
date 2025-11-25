package be.kdg.team22.userservice.domain.library;

import be.kdg.team22.userservice.infrastructure.library.jpa.LibraryEntryEntity;
import org.jmolecules.ddd.annotation.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LibraryRepository {

    List<LibraryEntryEntity> findByUserId(UUID userId);
}