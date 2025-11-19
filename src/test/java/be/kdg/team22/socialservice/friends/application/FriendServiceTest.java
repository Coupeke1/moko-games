package be.kdg.team22.socialservice.friends.application;

import be.kdg.team22.socialservice.friends.domain.Friendship;
import be.kdg.team22.socialservice.friends.domain.FriendshipRepository;
import be.kdg.team22.socialservice.friends.domain.FriendshipStatus;
import be.kdg.team22.socialservice.friends.domain.UserId;
import be.kdg.team22.socialservice.friends.infrastructure.http.UserClient;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class FriendServiceTest {

    private final FriendshipRepository friendshipRepository = mock(FriendshipRepository.class);
    private final UserClient userClient = mock(UserClient.class);

    private final FriendService friendService = new FriendService(friendshipRepository, userClient);

    private UserClient.UserResponse userResponse(UUID id, String username) {
        return new UserClient.UserResponse(id, username);
    }

    @Test
    void sendFriendRequest_createsNewWhenNoExisting() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UUID targetId = UUID.randomUUID();

        when(userClient.getByUsername("piet"))
                .thenReturn(userResponse(targetId, "piet"));

        when(friendshipRepository.findBetween(eq(currentUser), eq(UserId.from(targetId))))
                .thenReturn(Optional.empty());

        friendService.sendFriendRequest(currentUser, "piet");

        verify(friendshipRepository).findBetween(eq(currentUser), eq(UserId.from(targetId)));
        verify(friendshipRepository).save(any(Friendship.class));
    }

    @Test
    void sendFriendRequest_toSelfThrows() {
        UUID same = UUID.randomUUID();
        UserId currentUser = UserId.from(same);

        when(userClient.getByUsername("me"))
                .thenReturn(userResponse(same, "me"));

        assertThatThrownBy(() -> friendService.sendFriendRequest(currentUser, "me"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot add yourself");

        verify(friendshipRepository, never()).findBetween(any(), any());
        verify(friendshipRepository, never()).save(any());
    }

    @Test
    void sendFriendRequest_whenPendingThrows() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UUID targetId = UUID.randomUUID();
        UserId targetUser = UserId.from(targetId);

        Friendship existing = mock(Friendship.class);
        when(existing.status()).thenReturn(FriendshipStatus.PENDING);

        when(userClient.getByUsername("piet"))
                .thenReturn(userResponse(targetId, "piet"));
        when(friendshipRepository.findBetween(currentUser, targetUser))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> friendService.sendFriendRequest(currentUser, "piet"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("pending");

        verify(friendshipRepository, never()).save(any());
    }

    @Test
    void sendFriendRequest_whenAcceptedThrows() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UUID targetId = UUID.randomUUID();
        UserId targetUser = UserId.from(targetId);

        Friendship existing = mock(Friendship.class);
        when(existing.status()).thenReturn(FriendshipStatus.ACCEPTED);

        when(userClient.getByUsername("piet"))
                .thenReturn(userResponse(targetId, "piet"));
        when(friendshipRepository.findBetween(currentUser, targetUser))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> friendService.sendFriendRequest(currentUser, "piet"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already friends");

        verify(friendshipRepository, never()).save(any());
    }

    @Test
    void sendFriendRequest_resetsWhenRejected() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UUID targetId = UUID.randomUUID();
        UserId targetUser = UserId.from(targetId);

        Friendship existing = mock(Friendship.class);
        when(existing.status()).thenReturn(FriendshipStatus.REJECTED);

        when(userClient.getByUsername("piet"))
                .thenReturn(userResponse(targetId, "piet"));
        when(friendshipRepository.findBetween(currentUser, targetUser))
                .thenReturn(Optional.of(existing));

        friendService.sendFriendRequest(currentUser, "piet");

        verify(existing).resetToPending(currentUser, targetUser);
        verify(friendshipRepository).save(existing);
    }

    @Test
    void sendFriendRequest_resetsWhenCanceled() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UUID targetId = UUID.randomUUID();
        UserId targetUser = UserId.from(targetId);

        Friendship existing = mock(Friendship.class);
        when(existing.status()).thenReturn(FriendshipStatus.CANCELED);

        when(userClient.getByUsername("piet"))
                .thenReturn(userResponse(targetId, "piet"));
        when(friendshipRepository.findBetween(currentUser, targetUser))
                .thenReturn(Optional.of(existing));

        friendService.sendFriendRequest(currentUser, "piet");

        verify(existing).resetToPending(currentUser, targetUser);
        verify(friendshipRepository).save(existing);
    }

    @Test
    void acceptRequest_callsDomainAndSaves() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UUID otherId = UUID.randomUUID();
        UserId otherUser = UserId.from(otherId);

        Friendship friendship = mock(Friendship.class);

        when(friendshipRepository.findBetween(currentUser, otherUser))
                .thenReturn(Optional.of(friendship));

        friendService.acceptRequest(currentUser, otherId);

        verify(friendship).accept(currentUser);
        verify(friendshipRepository).save(friendship);
    }

    @Test
    void acceptRequest_whenNotFoundThrows() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UUID otherId = UUID.randomUUID();
        UserId otherUser = UserId.from(otherId);

        when(friendshipRepository.findBetween(currentUser, otherUser))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> friendService.acceptRequest(currentUser, otherId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No friendship");
    }

    @Test
    void rejectRequest_callsDomainAndSaves() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UUID otherId = UUID.randomUUID();
        UserId otherUser = UserId.from(otherId);

        Friendship friendship = mock(Friendship.class);

        when(friendshipRepository.findBetween(otherUser, currentUser))
                .thenReturn(Optional.of(friendship));

        friendService.rejectRequest(currentUser, otherId);

        verify(friendship).reject(currentUser);
        verify(friendshipRepository).save(friendship);
    }

    @Test
    void rejectRequest_whenNotFoundThrows() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UUID otherId = UUID.randomUUID();
        UserId otherUser = UserId.from(otherId);

        when(friendshipRepository.findBetween(otherUser, currentUser))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> friendService.rejectRequest(currentUser, otherId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No friendship");
    }

    @Test
    void cancelRequest_callsDomainAndSaves() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UUID otherId = UUID.randomUUID();
        UserId otherUser = UserId.from(otherId);

        Friendship friendship = mock(Friendship.class);

        when(friendshipRepository.findBetween(currentUser, otherUser))
                .thenReturn(Optional.of(friendship));

        friendService.cancelRequest(currentUser, otherId);

        verify(friendship).cancel(currentUser);
        verify(friendshipRepository).save(friendship);
    }

    @Test
    void cancelRequest_whenNotFoundThrows() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UUID otherId = UUID.randomUUID();
        UserId otherUser = UserId.from(otherId);

        when(friendshipRepository.findBetween(currentUser, otherUser))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> friendService.cancelRequest(currentUser, otherId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No friendship");
    }
}
