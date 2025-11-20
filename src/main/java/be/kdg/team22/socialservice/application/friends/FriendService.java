package be.kdg.team22.socialservice.application.friends;

import be.kdg.team22.socialservice.api.friends.models.FriendModel;
import be.kdg.team22.socialservice.api.friends.models.FriendsOverviewModel;
import be.kdg.team22.socialservice.application.friends.exceptions.CannotAddException;
import be.kdg.team22.socialservice.application.friends.exceptions.NotFoundException;
import be.kdg.team22.socialservice.domain.friends.friendship.Friendship;
import be.kdg.team22.socialservice.domain.friends.friendship.FriendshipRepository;
import be.kdg.team22.socialservice.domain.friends.friendship.FriendshipStatus;
import be.kdg.team22.socialservice.domain.friends.user.UserId;
import be.kdg.team22.socialservice.domain.friends.user.Username;
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

    public FriendService(FriendshipRepository friendshipRepository, ExternalUserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    public void sendRequest(UserId user, Username targetUsername) {
        UserResponse targetUserResponse = userRepository.getByUsername(targetUsername).orElseThrow(targetUsername::notFound);
        UserId targetUser = UserId.from(targetUserResponse.id());

        if (user.equals(targetUser))
            throw new CannotAddException(targetUsername);

        Friendship friendship = friendshipRepository.findBetween(user, targetUser).map(existingFriendship -> {
            existingFriendship.resetToPending(user, targetUser);
            return existingFriendship;
        }).orElseGet(() -> new Friendship(user, targetUser));

        friendshipRepository.save(friendship);
    }

    public void acceptRequest(UserId currentUser, UserId otherUser) {
        Friendship friendship = friendshipRepository.findBetween(currentUser, otherUser).orElseThrow(() -> NotFoundException.betweenFriendship(currentUser, otherUser));

        friendship.accept(currentUser);
        friendshipRepository.save(friendship);
    }

    public void rejectRequest(UserId user, UserId otherUser) {
        Friendship friendship = friendshipRepository.findBetween(user, otherUser).orElseThrow(NotFoundException::new);

        friendship.reject(user);
        friendshipRepository.save(friendship);
    }

    public void removeFriend(UserId currentUser, UserId otherUser) {
        Friendship friendship = friendshipRepository.findBetween(currentUser, otherUser).orElseThrow(() -> NotFoundException.betweenFriendship(currentUser, otherUser));

        friendship.checkCanRemove();
        friendshipRepository.delete(friendship);
    }

    public void cancelRequest(UserId currentUser, UserId otherUser) {
        Friendship friendship = friendshipRepository.findBetween(currentUser, otherUser).orElseThrow(() -> NotFoundException.betweenFriendship(currentUser, otherUser));

        friendship.cancel(currentUser);
        friendshipRepository.save(friendship);
    }

    public FriendsOverviewModel getOverview(UserId user) {
        List<Friendship> all = friendshipRepository.findAllFor(user);
        List<FriendModel> friends = all.stream().filter(f -> f.status() == FriendshipStatus.ACCEPTED).map(friendship -> toFriendModel(user, friendship)).toList();
        List<FriendModel> incoming = all.stream().filter(f -> f.status() == FriendshipStatus.PENDING && f.receiver().value().equals(user.value())).map(friendship -> toFriendModel(user, friendship)).toList();
        List<FriendModel> outgoing = all.stream().filter(f -> f.status() == FriendshipStatus.PENDING && f.requester().value().equals(user.value())).map(friendship -> toFriendModel(user, friendship)).toList();

        return new FriendsOverviewModel(friends, incoming, outgoing);
    }

    private FriendModel toFriendModel(UserId user, Friendship friendship) {
        UserId otherUser = friendship.otherSide(user);
        UserResponse userResponse = userRepository.getById(otherUser.value());

        return new FriendModel(otherUser.value(), userResponse.username(), friendship.status().name());
    }
}