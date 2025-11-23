package be.kdg.team22.socialservice.application.friends;

import be.kdg.team22.socialservice.api.friends.models.FriendModel;
import be.kdg.team22.socialservice.api.friends.models.FriendsOverviewModel;
import be.kdg.team22.socialservice.domain.friendship.Friendship;
import be.kdg.team22.socialservice.domain.friendship.FriendshipRepository;
import be.kdg.team22.socialservice.domain.friendship.FriendshipStatus;
import be.kdg.team22.socialservice.domain.friendship.exceptions.CannotAddException;
import be.kdg.team22.socialservice.domain.friendship.exceptions.FriendshipNotFoundException;
import be.kdg.team22.socialservice.domain.user.UserId;
import be.kdg.team22.socialservice.domain.user.Username;
import be.kdg.team22.socialservice.infrastructure.friends.user.ExternalUserRepository;
import be.kdg.team22.socialservice.infrastructure.friends.user.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class FriendService {
    private final FriendshipRepository friendshipRepository;
    private final ExternalUserRepository userRepository;

    public FriendService(final FriendshipRepository friendshipRepository, final ExternalUserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    public void sendRequest(final UserId userId, final Username username) {
        UserResponse response = userRepository.getByUsername(username).orElseThrow(username::notFound);
        UserId targetId = UserId.from(response.id());

        if (userId.equals(targetId))
            throw new CannotAddException(username);

        Friendship friendship = friendshipRepository.findBetween(userId, targetId).map(existingFriendship -> {
            existingFriendship.resetToPending(userId, targetId);
            return existingFriendship;
        }).orElseGet(() -> new Friendship(userId, targetId));

        friendshipRepository.save(friendship);
    }

    public void acceptRequest(final UserId userId, final UserId targetId) {
        Friendship friendship = friendshipRepository.findBetween(userId, targetId).orElseThrow(() -> FriendshipNotFoundException.betweenFriendship(userId, targetId));

        friendship.accept(userId);
        friendshipRepository.save(friendship);
    }

    public void rejectRequest(final UserId userId, final UserId targetId) {
        Friendship friendship = friendshipRepository.findBetween(userId, targetId).orElseThrow(() -> FriendshipNotFoundException.betweenFriendship(userId, targetId));

        friendship.reject(userId);
        friendshipRepository.save(friendship);
    }

    public void removeFriend(final UserId userId, final UserId targetId) {
        Friendship friendship = friendshipRepository.findBetween(userId, targetId).orElseThrow(() -> FriendshipNotFoundException.betweenFriendship(userId, targetId));

        friendship.checkCanRemove();
        friendshipRepository.delete(friendship);
    }

    public void cancelRequest(final UserId userId, final UserId targetId) {
        Friendship friendship = friendshipRepository.findBetween(userId, targetId).orElseThrow(() -> FriendshipNotFoundException.betweenFriendship(userId, targetId));

        friendship.cancel(userId);
        friendshipRepository.save(friendship);
    }

    public FriendsOverviewModel getOverview(final UserId userId) {
        List<Friendship> all = friendshipRepository.findAllFor(userId);
        List<FriendModel> friends = all.stream().filter(f -> f.status() == FriendshipStatus.ACCEPTED).map(friendship -> toFriendModel(userId, friendship)).toList();
        List<FriendModel> incoming = all.stream().filter(f -> f.status() == FriendshipStatus.PENDING && f.receiver().value().equals(userId.value())).map(friendship -> toFriendModel(userId, friendship)).toList();
        List<FriendModel> outgoing = all.stream().filter(f -> f.status() == FriendshipStatus.PENDING && f.requester().value().equals(userId.value())).map(friendship -> toFriendModel(userId, friendship)).toList();

        return new FriendsOverviewModel(friends, incoming, outgoing);
    }

    private FriendModel toFriendModel(final UserId userId, final Friendship friendship) {
        UserId user = friendship.otherSide(userId);
        UserResponse response = userRepository.getById(user.value());

        return new FriendModel(user.value(), response.username(), friendship.status().name());
    }
}