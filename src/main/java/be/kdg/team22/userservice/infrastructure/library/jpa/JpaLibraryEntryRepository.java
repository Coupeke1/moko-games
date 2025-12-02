package be.kdg.team22.userservice.infrastructure.library.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaLibraryEntryRepository extends JpaRepository<LibraryEntryEntity, UUID> {
    List<LibraryEntryEntity> findByUserId(UUID userId);

    Optional<LibraryEntryEntity> findByUserIdAndGameId(UUID userId, UUID gameId);
}
