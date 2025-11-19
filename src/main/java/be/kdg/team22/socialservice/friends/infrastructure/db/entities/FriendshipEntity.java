package be.kdg.team22.socialservice.friends.infrastructure.db.entities;

import be.kdg.team22.socialservice.friends.domain.Friendship;
import be.kdg.team22.socialservice.friends.domain.FriendshipId;
import be.kdg.team22.socialservice.friends.domain.FriendshipStatus;
import be.kdg.team22.socialservice.friends.domain.UserId;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "friendships",
        uniqueConstraints = @UniqueConstraint(columnNames = {"requester_id", "receiver_id"}))
public class FriendshipEntity {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "requester_id", nullable = false)
    private UUID requesterId;

    @Column(name = "receiver_id", nullable = false)
    private UUID receiverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendshipStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected FriendshipEntity() {
    }

    public FriendshipEntity(UUID id,
                            UUID requesterId,
                            UUID receiverId,
                            FriendshipStatus status,
                            Instant createdAt,
                            Instant updatedAt) {
        this.id = id;
        this.requesterId = requesterId;
        this.receiverId = receiverId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static FriendshipEntity fromDomain(Friendship f) {
        return new FriendshipEntity(
                f.id().value(),
                f.requester().value(),
                f.receiver().value(),
                f.status(),
                f.createdAt(),
                f.updatedAt()
        );
    }

    public Friendship toDomain() {
        return new Friendship(
                new FriendshipId(id),
                UserId.from(requesterId),
                UserId.from(receiverId),
                createdAt,
                updatedAt,
                status
        );
    }
}
