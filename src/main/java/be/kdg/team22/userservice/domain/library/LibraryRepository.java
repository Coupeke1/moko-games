package be.kdg.team22.userservice.domain.library;

import org.jmolecules.ddd.annotation.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LibraryRepository {
    List<LibraryEntry> findByUserId(UUID userId);

    Optional<LibraryEntry> findByUserIdAndGameId(UUID userId, UUID gameId);

    void save(LibraryEntry entry);
}