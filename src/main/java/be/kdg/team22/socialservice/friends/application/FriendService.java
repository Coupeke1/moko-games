package be.kdg.team22.socialservice.friends.application;

import be.kdg.team22.socialservice.friends.api.models.FriendModel;
import be.kdg.team22.socialservice.friends.api.models.FriendsOverviewModel;
import be.kdg.team22.socialservice.friends.domain.Friendship;
import be.kdg.team22.socialservice.friends.domain.FriendshipRepository;
import be.kdg.team22.socialservice.friends.domain.FriendshipStatus;
import be.kdg.team22.socialservice.friends.domain.UserId;
import be.kdg.team22.socialservice.friends.infrastructure.http.UserClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional
@Service
public class FriendService {

    private final FriendshipRepository friendshipRepository;
    private final UserClient userClient;

    public FriendService(FriendshipRepository friendshipRepository, UserClient userClient) {
        this.friendshipRepository = friendshipRepository;
        this.userClient = userClient;
    }

    public void sendFriendRequest(UserId currentUser, String targetUsername) {
        var targetUserResponse = userClient.getByUsername(targetUsername);
        var targetUser = UserId.from(targetUserResponse.id());

        if (currentUser.value().equals(targetUser.value())) {
            throw new IllegalArgumentException("Cannot add yourself as a friend");
        }

        friendshipRepository.findBetween(currentUser, targetUser)
                .ifPresent(f -> {
                    throw new IllegalStateException("Friendship already exists or is pending");
                });

        var friendship = Friendship.createNew(currentUser, targetUser);
        friendshipRepository.save(friendship);
    }

    public void acceptRequest(UserId currentUser, UUID otherUserId) {
        var other = UserId.from(otherUserId);

        var friendship = friendshipRepository.findBetween(currentUser, other)
                .orElseThrow(() -> new IllegalArgumentException("No friendship found"));

        friendship.accept(currentUser);

        friendshipRepository.save(friendship);
    }

    public void rejectRequest(UserId currentUser, UUID otherUserId) {
        var other = UserId.from(otherUserId);

        var friendship = friendshipRepository.findBetween(other, currentUser)
                .orElseThrow(() -> new IllegalArgumentException("No friendship found"));

        friendship.reject(currentUser);

        friendshipRepository.save(friendship);
    }

    public void removeFriend(UserId currentUser, UUID otherUserId) {
        var other = UserId.from(otherUserId);

        var friendship = friendshipRepository.findBetween(currentUser, other)
                .orElseThrow(() -> new IllegalArgumentException("No friendship found"));

        if (friendship.status() != FriendshipStatus.ACCEPTED) {
            throw new IllegalStateException("Cannot remove non-friend relationship");
        }

        friendshipRepository.delete(friendship);
    }

    public FriendsOverviewModel getOverview(UserId currentUser) {
        var all = friendshipRepository.findAllFor(currentUser);

        var friends = all.stream()
                .filter(f -> f.status() == FriendshipStatus.ACCEPTED)
                .map(f -> toFriendModel(currentUser, f))
                .toList();

        var incoming = all.stream()
                .filter(f -> f.status() == FriendshipStatus.PENDING &&
                        f.receiver().value().equals(currentUser.value()))
                .map(f -> toFriendModel(currentUser, f))
                .toList();

        var outgoing = all.stream()
                .filter(f -> f.status() == FriendshipStatus.PENDING &&
                        f.requester().value().equals(currentUser.value()))
                .map(f -> toFriendModel(currentUser, f))
                .toList();

        return new FriendsOverviewModel(friends, incoming, outgoing);
    }

    private FriendModel toFriendModel(UserId currentUser, Friendship f) {
        var other = f.otherSide(currentUser);
        var userResponse = userClient.getById(other.value());

        return new FriendModel(
                other.value().toString(),
                userResponse.username(),
                f.status().name()
        );
    }
}
