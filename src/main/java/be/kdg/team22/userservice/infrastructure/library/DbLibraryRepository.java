package be.kdg.team22.userservice.infrastructure.library;

import be.kdg.team22.userservice.domain.library.LibraryEntry;
import be.kdg.team22.userservice.domain.library.LibraryRepository;
import be.kdg.team22.userservice.infrastructure.library.jpa.JpaLibraryEntryRepository;
import be.kdg.team22.userservice.infrastructure.library.jpa.LibraryEntryEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class DbLibraryRepository implements LibraryRepository {

    private final JpaLibraryEntryRepository jpa;

    public DbLibraryRepository(JpaLibraryEntryRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<LibraryEntry> findByUserId(UUID userId) {
        return jpa.findByUserId(userId)
                .stream()
                .map(LibraryEntryEntity::toDomain)
                .toList();
    }
}