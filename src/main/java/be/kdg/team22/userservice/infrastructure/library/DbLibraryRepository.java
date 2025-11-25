package be.kdg.team22.userservice.infrastructure.library;

import be.kdg.team22.userservice.domain.library.LibraryRepository;
import be.kdg.team22.userservice.infrastructure.library.jpa.JpaLibraryEntryRepository;
import be.kdg.team22.userservice.infrastructure.library.jpa.LibraryEntryEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class DbLibraryRepository implements LibraryRepository {
    private final JpaLibraryEntryRepository repository;

    public DbLibraryRepository(final JpaLibraryEntryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<LibraryEntryEntity> findByUserId(UUID userId) {
        return repository.findByUserId(userId);
    }
}