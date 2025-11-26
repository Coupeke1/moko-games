package be.kdg.team22.socialservice.applicatiton.friends;

import be.kdg.team22.socialservice.api.friends.models.FriendModel;
import be.kdg.team22.socialservice.api.friends.models.FriendsOverviewModel;
import be.kdg.team22.socialservice.application.friends.FriendService;
import be.kdg.team22.socialservice.domain.friendship.exceptions.CannotAddException;
import be.kdg.team22.socialservice.domain.friendship.exceptions.CannotRemoveException;
import be.kdg.team22.socialservice.domain.friendship.Friendship;
import be.kdg.team22.socialservice.domain.friendship.FriendshipId;
import be.kdg.team22.socialservice.domain.friendship.FriendshipRepository;
import be.kdg.team22.socialservice.domain.friendship.FriendshipStatus;
import be.kdg.team22.socialservice.domain.friendship.exceptions.FriendshipNotFoundException;
import be.kdg.team22.socialservice.domain.user.UserId;
import be.kdg.team22.socialservice.domain.user.Username;
import be.kdg.team22.socialservice.infrastructure.user.ExternalUserRepository;
import be.kdg.team22.socialservice.infrastructure.user.UserResponse;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class FriendServiceTest {
    private final FriendshipRepository friendshipRepository = mock(FriendshipRepository.class);
    private final ExternalUserRepository userRepository = mock(ExternalUserRepository.class);
    private final FriendService service = new FriendService(friendshipRepository, userRepository);

    private UserResponse userResponse(UUID id, String username) {
        return new UserResponse(id, username);
    }

    @Test
    void send_Request_createsNewWhenNoExisting() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UUID targetId = UUID.randomUUID();

        when(userRepository.getByUsername(new Username("piet"))).thenReturn(Optional.of(userResponse(targetId, "piet")));

        when(friendshipRepository.findBetween(eq(currentUser), eq(UserId.from(targetId)))).thenReturn(Optional.empty());

        service.sendRequest(currentUser, new Username("piet"));

        verify(friendshipRepository).findBetween(eq(currentUser), eq(UserId.from(targetId)));
        verify(friendshipRepository).save(any(Friendship.class));
    }

    @Test
    void send_Request_toSelfThrows() {
        UUID same = UUID.randomUUID();
        UserId currentUser = UserId.from(same);

        when(userRepository.getByUsername(new Username("me"))).thenReturn(Optional.of(userResponse(same, "me")));

        assertThatThrownBy(() -> service.sendRequest(currentUser, new Username("me"))).isInstanceOf(CannotAddException.class);

        verify(friendshipRepository, never()).findBetween(any(), any());
        verify(friendshipRepository, never()).save(any());

        verify(userRepository).getByUsername(new Username("me"));
    }

    @Test
    void send_Request_whenAcceptedThrows() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UUID targetId = UUID.randomUUID();
        UserId targetUser = UserId.from(targetId);

        Friendship existing = mock(Friendship.class);
        when(existing.status()).thenReturn(FriendshipStatus.ACCEPTED);
        doThrow(new IllegalStateException("You are already friends")).when(existing).resetToPending(currentUser, targetUser);

        when(userRepository.getByUsername(new Username("piet"))).thenReturn(Optional.of(userResponse(targetId, "piet")));
        when(friendshipRepository.findBetween(currentUser, targetUser)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.sendRequest(currentUser, new Username("piet"))).isInstanceOf(IllegalStateException.class).hasMessageContaining("already friends");

        verify(friendshipRepository, never()).save(any());
    }

    @Test
    void send_Request_resetsWhenRejected() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UUID targetId = UUID.randomUUID();
        UserId targetUser = UserId.from(targetId);

        Friendship existing = mock(Friendship.class);
        when(existing.status()).thenReturn(FriendshipStatus.REJECTED);

        when(userRepository.getByUsername(new Username("piet"))).thenReturn(Optional.of(userResponse(targetId, "piet")));
        when(friendshipRepository.findBetween(currentUser, targetUser)).thenReturn(Optional.of(existing));

        service.sendRequest(currentUser, new Username("piet"));

        verify(existing).resetToPending(currentUser, targetUser);
        verify(friendshipRepository).save(existing);
    }

    @Test
    void send_Request_resetsWhenCanceled() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UUID targetId = UUID.randomUUID();
        UserId targetUser = UserId.from(targetId);

        Friendship existing = mock(Friendship.class);
        when(existing.status()).thenReturn(FriendshipStatus.CANCELLED);

        when(userRepository.getByUsername(new Username("piet"))).thenReturn(Optional.of(userResponse(targetId, "piet")));
        when(friendshipRepository.findBetween(currentUser, targetUser)).thenReturn(Optional.of(existing));

        service.sendRequest(currentUser, new Username("piet"));

        verify(existing).resetToPending(currentUser, targetUser);
        verify(friendshipRepository).save(existing);
    }

    @Test
    void acceptRequest_callsDomainAndSaves() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UUID otherId = UUID.randomUUID();
        UserId otherUser = UserId.from(otherId);

        Friendship friendship = mock(Friendship.class);

        when(friendshipRepository.findBetween(currentUser, otherUser)).thenReturn(Optional.of(friendship));

        service.acceptRequest(currentUser, otherUser);

        verify(friendship).accept(currentUser);
        verify(friendshipRepository).save(friendship);
    }

    @Test
    void acceptRequest_whenNotFoundThrows() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UUID otherId = UUID.randomUUID();
        UserId otherUser = UserId.from(otherId);

        when(friendshipRepository.findBetween(currentUser, otherUser)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.acceptRequest(currentUser, otherUser)).isInstanceOf(FriendshipNotFoundException.class);
    }

    @Test
    void rejectRequest_whenNotFoundThrows() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UserId otherUser = UserId.from(UUID.randomUUID());

        when(friendshipRepository.findBetween(otherUser, currentUser)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.rejectRequest(currentUser, otherUser)).isInstanceOf(FriendshipNotFoundException.class);
    }

    @Test
    void cancelRequest_callsDomainAndSaves() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UserId otherUser = UserId.from(UUID.randomUUID());

        Friendship friendship = mock(Friendship.class);

        when(friendshipRepository.findBetween(currentUser, otherUser)).thenReturn(Optional.of(friendship));
        service.cancelRequest(currentUser, otherUser);

        verify(friendship).cancel(currentUser);
        verify(friendshipRepository).save(friendship);
    }

    @Test
    void cancelRequest_whenNotFoundThrows() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UserId otherUser = UserId.from(UUID.randomUUID());

        when(friendshipRepository.findBetween(currentUser, otherUser)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.cancelRequest(currentUser, otherUser)).isInstanceOf(FriendshipNotFoundException.class);
    }

    @Test
    void removeFriend_deletesWhenAccepted() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UserId otherUser = UserId.from(UUID.randomUUID());

        Friendship friendship = mock(Friendship.class);
        when(friendship.status()).thenReturn(FriendshipStatus.ACCEPTED);

        when(friendshipRepository.findBetween(currentUser, otherUser)).thenReturn(Optional.of(friendship));

        service.removeFriend(currentUser, otherUser);

        verify(friendshipRepository).delete(friendship);
    }

    @Test
    void removeFriend_whenNotAcceptedThrows() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UserId otherUser = UserId.from(UUID.randomUUID());

        Friendship friendship = mock(Friendship.class);
        when(friendship.status()).thenReturn(FriendshipStatus.PENDING);
        doCallRealMethod().when(friendship).checkCanRemove();

        when(friendshipRepository.findBetween(currentUser, otherUser)).thenReturn(Optional.of(friendship));

        assertThatThrownBy(() -> service.removeFriend(currentUser, otherUser)).isInstanceOf(CannotRemoveException.class);

        verify(friendshipRepository, never()).delete(any());
    }

    @Test
    void removeFriend_whenNotFoundThrows() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UserId otherUser = UserId.from(UUID.randomUUID());

        when(friendshipRepository.findBetween(currentUser, otherUser)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.removeFriend(currentUser, otherUser)).isInstanceOf(FriendshipNotFoundException.class);
    }

    @Test
    void getOverview_groupsFriendsIncomingAndOutgoing() {
        UserId current = UserId.from(UUID.randomUUID());
        UserId friend = UserId.from(UUID.randomUUID());
        UserId incoming = UserId.from(UUID.randomUUID());
        UserId outgoing = UserId.from(UUID.randomUUID());

        Friendship accepted = new Friendship(FriendshipId.create(), current, friend, Instant.now(), Instant.now(), FriendshipStatus.PENDING);
        accepted.accept(friend);

        Friendship incomingReq = new Friendship(FriendshipId.create(), incoming, current, Instant.now(), Instant.now(), FriendshipStatus.PENDING);

        Friendship outgoingReq = new Friendship(FriendshipId.create(), current, outgoing, Instant.now(), Instant.now(), FriendshipStatus.PENDING);

        when(friendshipRepository.findAllFor(current)).thenReturn(List.of(accepted, incomingReq, outgoingReq));

        when(userRepository.getById(friend.value())).thenReturn(new UserResponse(friend.value(), "friendUser"));
        when(userRepository.getById(incoming.value())).thenReturn(new UserResponse(incoming.value(), "incomingUser"));
        when(userRepository.getById(outgoing.value())).thenReturn(new UserResponse(outgoing.value(), "outgoingUser"));

        FriendsOverviewModel overview = service.getOverview(current);

        assertThat(overview.friends()).hasSize(1);
        FriendModel friendModel = overview.friends().getFirst();
        assertThat(friendModel.id()).isEqualTo(friend.value());
        assertThat(friendModel.username()).isEqualTo("friendUser");
        assertThat(friendModel.status()).isEqualTo(FriendshipStatus.ACCEPTED.name());

        // incoming (PENDING where current == receiver)
        assertThat(overview.incoming()).hasSize(1);
        FriendModel incomingModel = overview.incoming().getFirst();
        assertThat(incomingModel.id()).isEqualTo(incoming.value());
        assertThat(incomingModel.username()).isEqualTo("incomingUser");
        assertThat(incomingModel.status()).isEqualTo(FriendshipStatus.PENDING.name());

        assertThat(overview.outgoing()).hasSize(1);
        FriendModel outgoingModel = overview.outgoing().getFirst();
        assertThat(outgoingModel.id()).isEqualTo(outgoing.value());
        assertThat(outgoingModel.username()).isEqualTo("outgoingUser");
        assertThat(outgoingModel.status()).isEqualTo(FriendshipStatus.PENDING.name());

        verify(userRepository).getById(friend.value());
        verify(userRepository).getById(incoming.value());
        verify(userRepository).getById(outgoing.value());
    }

    @Test
    void sendRequest_whenPendingThrows() {
        UserId currentUser = UserId.from(UUID.randomUUID());
        UUID targetId = UUID.randomUUID();
        UserId targetUser = UserId.from(targetId);

        Friendship existing = mock(Friendship.class);
        when(existing.status()).thenReturn(FriendshipStatus.PENDING);
        // resetToPending() is what actually throws → so we mock that behavior
        doThrow(new IllegalStateException("Friend request already pending")).when(existing).resetToPending(currentUser, targetUser);

        when(userRepository.getByUsername(new Username("piet"))).thenReturn(Optional.of(userResponse(targetId, "piet")));
        when(friendshipRepository.findBetween(currentUser, targetUser)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.sendRequest(currentUser, new Username("piet"))).isInstanceOf(IllegalStateException.class).hasMessageContaining("pending");

        verify(friendshipRepository, never()).save(any());
    }
}
