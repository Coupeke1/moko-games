package be.kdg.team22.userservice.domain.library;

import be.kdg.team22.userservice.domain.library.exceptions.LibraryException;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import org.jmolecules.ddd.annotation.Entity;

import java.time.Instant;
import java.util.UUID;

@Entity
public record LibraryEntry(LibraryId id,
                           ProfileId userId,
                           GameId gameId,
                           Instant purchasedAt,
                           boolean favourite) {
    public LibraryEntry {
        if (userId == null)
            throw LibraryException.missingUserId();
        if (gameId == null)
            throw LibraryException.missingGameId();
        if (purchasedAt == null)
            throw LibraryException.missingPurchasedAt();
    }

    public LibraryEntry markFavourite() {
        return new LibraryEntry(id, userId, gameId, purchasedAt, true);
    }

    public LibraryEntry unmarkFavourite() {
        return new LibraryEntry(id, userId, gameId, purchasedAt, false);
    }
}