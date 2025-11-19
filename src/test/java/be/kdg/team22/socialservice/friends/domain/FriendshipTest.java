package be.kdg.team22.socialservice.friends.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class FriendshipTest {

    private UserId user(UUID id) { return UserId.from(id); }

    @Test
    void createNew_initializesCorrectly() {
        UserId requester = user(UUID.randomUUID());
        UserId receiver = user(UUID.randomUUID());

        Friendship f = new Friendship(requester, receiver);

        assertThat(f.requester()).isEqualTo(requester);
        assertThat(f.receiver()).isEqualTo(receiver);
        assertThat(f.status()).isEqualTo(FriendshipStatus.PENDING);
        assertThat(f.createdAt()).isNotNull();
        assertThat(f.updatedAt()).isNotNull();
    }

    @Test
    void accept_valid() {
        UserId requester = user(UUID.randomUUID());
        UserId receiver = user(UUID.randomUUID());

        Friendship f = new Friendship(
                FriendshipId.newId(), requester, receiver,
                Instant.now(), Instant.now(), FriendshipStatus.PENDING
        );

        f.accept(receiver);
        assertThat(f.status()).isEqualTo(FriendshipStatus.ACCEPTED);
    }

    @Test
    void accept_wrongUserThrows() {
        UserId requester = user(UUID.randomUUID());
        UserId receiver = user(UUID.randomUUID());
        UserId other = user(UUID.randomUUID());

        Friendship f = new Friendship(
                FriendshipId.newId(), requester, receiver,
                Instant.now(), Instant.now(), FriendshipStatus.PENDING
        );

        assertThatThrownBy(() -> f.accept(other))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void reject_valid() {
        UserId requester = user(UUID.randomUUID());
        UserId receiver = user(UUID.randomUUID());

        Friendship f = new Friendship(
                FriendshipId.newId(), requester, receiver,
                Instant.now(), Instant.now(), FriendshipStatus.PENDING
        );

        f.reject(receiver);
        assertThat(f.status()).isEqualTo(FriendshipStatus.REJECTED);
    }

    @Test
    void cancel_valid() {
        UserId requester = user(UUID.randomUUID());
        UserId receiver = user(UUID.randomUUID());

        Friendship f = new Friendship(
                FriendshipId.newId(), requester, receiver,
                Instant.now(), Instant.now(), FriendshipStatus.PENDING
        );

        f.cancel(requester);
        assertThat(f.status()).isEqualTo(FriendshipStatus.CANCELED);
    }

    @Test
    void resetToPending_updatesState() {
        UserId a = user(UUID.randomUUID());
        UserId b = user(UUID.randomUUID());
        UserId c = user(UUID.randomUUID());
        UserId d = user(UUID.randomUUID());

        Friendship f = new Friendship(a, b);
        f.reject(b);

        f.resetToPending(c, d);

        assertThat(f.status()).isEqualTo(FriendshipStatus.PENDING);
        assertThat(f.requester()).isEqualTo(c);
        assertThat(f.receiver()).isEqualTo(d);
    }

    @Test
    void involves_checksBothSides() {
        UserId a = user(UUID.randomUUID());
        UserId b = user(UUID.randomUUID());
        UserId c = user(UUID.randomUUID());

        Friendship f = new Friendship(a, b);

        assertThat(f.involves(a)).isTrue();
        assertThat(f.involves(b)).isTrue();
        assertThat(f.involves(c)).isFalse();
    }

    @Test
    void otherSide_returnsCorrectUser() {
        UserId a = user(UUID.randomUUID());
        UserId b = user(UUID.randomUUID());

        Friendship f = new Friendship(a, b);

        assertThat(f.otherSide(a)).isEqualTo(b);
        assertThat(f.otherSide(b)).isEqualTo(a);

        UserId c = user(UUID.randomUUID());
        assertThatThrownBy(() -> f.otherSide(c))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
