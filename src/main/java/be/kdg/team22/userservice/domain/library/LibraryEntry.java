package be.kdg.team22.userservice.domain.library;

import org.jmolecules.ddd.annotation.Entity;

import java.time.Instant;
import java.util.UUID;

@Entity
public record LibraryEntry(UUID id, UUID userId, UUID gameId, Instant purchasedAt) {

    public LibraryEntry {
        if (userId == null) throw new IllegalArgumentException("userId cannot be null");
        if (gameId == null) throw new IllegalArgumentException("gameId cannot be null");
        if (purchasedAt == null) throw new IllegalArgumentException("purchasedAt cannot be null");

    }
}
