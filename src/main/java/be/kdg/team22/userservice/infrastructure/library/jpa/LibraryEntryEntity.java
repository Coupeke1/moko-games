package be.kdg.team22.userservice.infrastructure.library.jpa;

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

    protected LibraryEntryEntity() {
    }

    public LibraryEntryEntity(UUID userId, UUID gameId, Instant purchasedAt) {
        this.userId = userId;
        this.gameId = gameId;
        this.purchasedAt = purchasedAt;
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
}
