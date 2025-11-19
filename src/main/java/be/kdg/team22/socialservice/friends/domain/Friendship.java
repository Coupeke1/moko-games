package be.kdg.team22.socialservice.friends.domain;

import java.time.Instant;
import java.util.Objects;

public class Friendship {

    private final FriendshipId id;
    private final UserId requester;
    private final UserId receiver;
    private final Instant createdAt;
    private Instant updatedAt;
    private FriendshipStatus status;

    public Friendship(FriendshipId id,
                      UserId requester,
                      UserId receiver,
                      Instant createdAt,
                      Instant updatedAt,
                      FriendshipStatus status) {

        this.id = Objects.requireNonNull(id);
        this.requester = Objects.requireNonNull(requester);
        this.receiver = Objects.requireNonNull(receiver);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
        this.status = Objects.requireNonNull(status);
    }

    public static Friendship createNew(UserId requester, UserId receiver) {
        return new Friendship(
                FriendshipId.newId(),
                requester,
                receiver,
                Instant.now(),
                Instant.now(),
                FriendshipStatus.PENDING
        );
    }

    public void accept(UserId actingUser) {
        if (!actingUser.value().equals(receiver.value()))
            throw new IllegalStateException("Only receiver can accept");
        if (status != FriendshipStatus.PENDING)
            throw new IllegalStateException("Only pending requests can be accepted");

        this.status = FriendshipStatus.ACCEPTED;
        this.updatedAt = Instant.now();
    }

    public void reject(UserId actingUser) {
        if (!actingUser.value().equals(receiver.value()))
            throw new IllegalStateException("Only receiver can reject");
        if (status != FriendshipStatus.PENDING)
            throw new IllegalStateException("Only pending requests can be rejected");

        this.status = FriendshipStatus.REJECTED;
        this.updatedAt = Instant.now();
    }

    public FriendshipId id() {
        return id;
    }

    public UserId requester() {
        return requester;
    }

    public UserId receiver() {
        return receiver;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public FriendshipStatus status() {
        return status;
    }

    public boolean involves(UserId user) {
        return requester.value().equals(user.value()) ||
                receiver.value().equals(user.value());
    }

    public UserId otherSide(UserId user) {
        if (requester.value().equals(user.value())) return receiver;
        if (receiver.value().equals(user.value())) return requester;
        throw new IllegalArgumentException("User is not part of this friendship");
    }
}
