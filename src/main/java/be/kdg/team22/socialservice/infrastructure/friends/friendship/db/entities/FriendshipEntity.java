package be.kdg.team22.socialservice.infrastructure.friends.friendship.db.entities;

import be.kdg.team22.socialservice.domain.friends.friendship.Friendship;
import be.kdg.team22.socialservice.domain.friends.friendship.FriendshipId;
import be.kdg.team22.socialservice.domain.friends.friendship.FriendshipStatus;
import be.kdg.team22.socialservice.domain.friends.user.UserId;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "friendships", uniqueConstraints = {@UniqueConstraint(name = "friendships_requester_id_receiver_id_key", columnNames = {"requester_id", "receiver_id"})})
public class FriendshipEntity {
    @Id
    private UUID id;

    @Column(name = "requester_id", nullable = false)
    private UUID requesterId;

    @Column(name = "receiver_id", nullable = false)
    private UUID receiverId;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected FriendshipEntity() {
    }

    public FriendshipEntity(UUID id, UUID requesterId, UUID receiverId, FriendshipStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.requesterId = requesterId;
        this.receiverId = receiverId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static FriendshipEntity fromDomain(Friendship friendship) {
        return new FriendshipEntity(friendship.id().value(), friendship.requester().value(), friendship.receiver().value(), friendship.status(), friendship.createdAt(), friendship.updatedAt());
    }

    public Friendship toDomain() {
        return new Friendship(new FriendshipId(id), UserId.from(requesterId), UserId.from(receiverId), createdAt, updatedAt, status);
    }
}