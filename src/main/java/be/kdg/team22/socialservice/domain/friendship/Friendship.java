package be.kdg.team22.socialservice.domain.friendship;

import be.kdg.team22.socialservice.domain.friendship.exceptions.CannotAcceptException;
import be.kdg.team22.socialservice.domain.friendship.exceptions.CannotRemoveException;
import be.kdg.team22.socialservice.domain.friendship.exceptions.NoMatchingUsersException;
import be.kdg.team22.socialservice.domain.user.UserId;
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

    public Friendship(final FriendshipId id, final UserId requester, final UserId receiver, final Instant createdAt, final Instant updatedAt, final FriendshipStatus status) {
        this.id = id;
        this.requester = requester;
        this.receiver = receiver;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
    }

    public Friendship(final UserId requester, final UserId receiver) {
        this(FriendshipId.create(), requester, receiver, Instant.now(), Instant.now(), FriendshipStatus.PENDING);
    }

    public void accept(final UserId user) {
        checkBeforeChanging(user);

        this.status = FriendshipStatus.ACCEPTED;
        this.updatedAt = Instant.now();
    }

    public void reject(final UserId user) {
        checkBeforeChanging(user);

        this.status = FriendshipStatus.REJECTED;
        this.updatedAt = Instant.now();
    }

    public void cancel(final UserId user) {
        checkBeforeChanging(user);

        this.status = FriendshipStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    public void resetToPending(final UserId requester, final UserId receiver) {
        if (!requester.equals(this.requester) || !receiver.equals(this.receiver)) {
            throw new NoMatchingUsersException();
        }

        this.status = FriendshipStatus.PENDING;
        this.updatedAt = Instant.now();
    }

    public boolean involves(final UserId user) {
        return requester.value().equals(user.value()) || receiver.value().equals(user.value());
    }

    public UserId otherSide(final UserId user) {
        if (requester.value().equals(user.value()))
            return receiver;
        if (receiver.value().equals(user.value()))
            return requester;

        throw new NoMatchingUsersException();
    }

    public void checkCanRemove() {
        if (status == FriendshipStatus.ACCEPTED)
            return;

        throw new CannotRemoveException();
    }

    private void checkBeforeChanging(final UserId user) {
        boolean isRequesterOrReceiver = user.value().equals(requester.value()) || user.value().equals(receiver.value());

        if (!isRequesterOrReceiver || status != FriendshipStatus.PENDING) {
            throw new CannotAcceptException();
        }
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