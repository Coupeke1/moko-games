package be.kdg.team22.socialservice.domain.friends.friendship;

import be.kdg.team22.socialservice.domain.friends.friendship.exceptions.CannotAcceptException;
import be.kdg.team22.socialservice.domain.friends.friendship.exceptions.NoMatchingUsersException;
import be.kdg.team22.socialservice.domain.friends.user.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.time.Instant;

@AggregateRoot
public class Friendship {
    private final FriendshipId id;
    private final UserId requester;
    private final UserId receiver;
    private final Instant createdAt;
    private Instant updatedAt;
    private FriendshipStatus status;

    public Friendship(FriendshipId id, UserId requester, UserId receiver, Instant createdAt, Instant updatedAt, FriendshipStatus status) {
        this.id = id;
        this.requester = requester;
        this.receiver = receiver;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
    }

    public Friendship(UserId requester, UserId receiver) {
        this(FriendshipId.create(), requester, receiver, Instant.now(), Instant.now(), FriendshipStatus.PENDING);
    }

    public void accept(UserId user) {
        checkBeforeChanging(user);

        this.status = FriendshipStatus.ACCEPTED;
        this.updatedAt = Instant.now();
    }

    public void reject(UserId user) {
        checkBeforeChanging(user);

        this.status = FriendshipStatus.REJECTED;
        this.updatedAt = Instant.now();
    }

    public void cancel(UserId user) {
        checkBeforeChanging(user);

        this.status = FriendshipStatus.CANCELED;
        this.updatedAt = Instant.now();
    }

    public void resetToPending(UserId requester, UserId receiver) {
        if (!requester.equals(this.requester) || !receiver.equals(this.receiver)) {
            throw new NoMatchingUsersException();
        }

        this.status = FriendshipStatus.PENDING;
        this.updatedAt = Instant.now();
    }

    public boolean involves(UserId user) {
        return requester.value().equals(user.value()) || receiver.value().equals(user.value());
    }

    public UserId otherSide(UserId user) {
        if (requester.value().equals(user.value()))
            return receiver;

        if (receiver.value().equals(user.value()))
            return requester;

        throw new NoMatchingUsersException();
    }

    private void checkBeforeChanging(UserId user) {
        if (!user.value().equals(receiver.value()) || status != FriendshipStatus.PENDING)
            throw new CannotAcceptException();
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

    public FriendshipStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}