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

import java.util.List;
import java.util.Optional;
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
        UserClient.UserResponse targetUserResponse = userClient.getByUsername(targetUsername);
        UserId targetUser = UserId.from(targetUserResponse.id());

        if (currentUser.value().equals(targetUser.value())) {
            throw new IllegalArgumentException("Cannot add yourself as a friend");
        }

        Optional<Friendship> existingFriendship = friendshipRepository.findBetween(currentUser, targetUser);

        if (existingFriendship.isPresent()) {
            Friendship existing = existingFriendship.get();

            switch (existing.status()) {
                case PENDING -> throw new IllegalStateException("Friend request already pending");
                case ACCEPTED -> throw new IllegalStateException("You are already friends");

                case REJECTED, CANCELED -> {
                    existing.resetToPending(currentUser, targetUser);
                    friendshipRepository.save(existing);
                    return;
                }
            }
        }

        Friendship friendship = new Friendship(currentUser, targetUser);
        friendshipRepository.save(friendship);
    }

    public void acceptRequest(UserId currentUser, UUID otherUserId) {
        UserId other = UserId.from(otherUserId);

        Friendship friendship = friendshipRepository.findBetween(currentUser, other)
                .orElseThrow(() -> new IllegalArgumentException("No friendship found"));

        friendship.accept(currentUser);

        friendshipRepository.save(friendship);
    }

    public void rejectRequest(UserId currentUser, UUID otherUserId) {
        UserId other = UserId.from(otherUserId);

        Friendship friendship = friendshipRepository.findBetween(other, currentUser)
                .orElseThrow(() -> new IllegalArgumentException("No friendship found"));

        friendship.reject(currentUser);

        friendshipRepository.save(friendship);
    }

    public void removeFriend(UserId currentUser, UUID otherUserId) {
        UserId other = UserId.from(otherUserId);

        Friendship friendship = friendshipRepository.findBetween(currentUser, other)
                .orElseThrow(() -> new IllegalArgumentException("No friendship found"));

        if (friendship.status() != FriendshipStatus.ACCEPTED) {
            throw new IllegalStateException("Cannot remove non-friend relationship");
        }

        friendshipRepository.delete(friendship);
    }

    public void cancelRequest(UserId currentUser, UUID otherUserId) {
        UserId other = UserId.from(otherUserId);
        Friendship friendship = friendshipRepository.findBetween(currentUser, other)
                .orElseThrow(() -> new IllegalArgumentException("No friendship found"));

        friendship.cancel(currentUser);

        friendshipRepository.save(friendship);
    }

    public FriendsOverviewModel getOverview(UserId currentUser) {
        List<Friendship> all = friendshipRepository.findAllFor(currentUser);

        List<FriendModel> friends = all.stream()
                .filter(f -> f.status() == FriendshipStatus.ACCEPTED)
                .map(friendship -> toFriendModel(currentUser, friendship))
                .toList();

        List<FriendModel> incoming = all.stream()
                .filter(f -> f.status() == FriendshipStatus.PENDING &&
                        f.receiver().value().equals(currentUser.value()))
                .map(friendship -> toFriendModel(currentUser, friendship))
                .toList();

        List<FriendModel> outgoing = all.stream()
                .filter(f -> f.status() == FriendshipStatus.PENDING &&
                        f.requester().value().equals(currentUser.value()))
                .map(friendship -> toFriendModel(currentUser, friendship))
                .toList();

        return new FriendsOverviewModel(friends, incoming, outgoing);
    }

    private FriendModel toFriendModel(UserId currentUser, Friendship f) {
        UserId other = f.otherSide(currentUser);
        UserClient.UserResponse userResponse = userClient.getById(other.value());

        return new FriendModel(
                other.value(),
                userResponse.username(),
                f.status().name()
        );
    }
}
