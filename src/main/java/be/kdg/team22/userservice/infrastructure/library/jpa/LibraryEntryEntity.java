package be.kdg.team22.userservice.infrastructure.library.jpa;

import be.kdg.team22.userservice.domain.library.GameId;
import be.kdg.team22.userservice.domain.library.LibraryEntry;
import be.kdg.team22.userservice.domain.library.LibraryId;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_library")
public class LibraryEntryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "user_id")
    private UUID userId;

    @Column(nullable = false, name = "game_id")
    private UUID gameId;

    @Column(nullable = false, name = "purchased_at")
    private Instant purchasedAt;

    @Column(nullable = false)
    private boolean favourite = false;

    protected LibraryEntryEntity() {
    }

    public LibraryEntryEntity(final UUID id, final UUID userId, final UUID gameId, final Instant purchasedAt, final boolean favourite) {
        this.id = id;
        this.userId = userId;
        this.gameId = gameId;
        this.purchasedAt = purchasedAt;
        this.favourite = favourite;
    }

    public static LibraryEntryEntity fromDomain(final LibraryEntry entry) {
        return new LibraryEntryEntity(entry.id().value(), entry.userId().value(), entry.gameId().value(), entry.purchasedAt(), entry.favourite());
    }

    public LibraryEntry toDomain() {
        return new LibraryEntry(new LibraryId(id), new ProfileId(userId), new GameId(gameId), purchasedAt, favourite);
    }

    public UUID id() {
        return id;
    }

    public UUID userId() {
        return userId;
    }

    public UUID gameId() {
        return gameId;
    }

    public Instant purchasedAt() {
        return purchasedAt;
    }

    public boolean favourite() {
        return favourite;
    }
}