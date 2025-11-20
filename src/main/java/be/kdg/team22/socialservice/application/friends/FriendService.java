package be.kdg.team22.socialservice.application.friends;

import be.kdg.team22.socialservice.api.friends.models.FriendModel;
import be.kdg.team22.socialservice.api.friends.models.FriendsOverviewModel;
import be.kdg.team22.socialservice.application.friends.exceptions.*;
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
import java.util.Optional;
import java.util.UUID;

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
        UserResponse response = userRepository.getByUsername(targetUsername);
        UserId targetUser = UserId.from(response.id());

        if (user.value().equals(targetUser.value()))
            throw new CannotAddException(targetUsername);

        Optional<Friendship> existingFriendship = friendshipRepository.findBetween(user, targetUser);

        if (existingFriendship.isPresent()) {
            Friendship existing = existingFriendship.get();

            switch (existing.status()) {
                case PENDING ->
                        throw new AlreadyPendingException(existing.id());
                case ACCEPTED ->
                        throw new AlreadyFriendsException(targetUsername);

                case REJECTED, CANCELED -> {
                    existing.resetToPending(user, targetUser);
                    friendshipRepository.save(existing);
                    return;
                }
            }
        }

        Friendship friendship = new Friendship(user, targetUser);
        friendshipRepository.save(friendship);
    }

    public void acceptRequest(UserId user, UserId otherUser) {
        Friendship friendship = friendshipRepository.findBetween(user, otherUser).orElseThrow(NotFoundException::new);

        friendship.accept(user);
        friendshipRepository.save(friendship);
    }

    public void rejectRequest(UserId user, UserId otherUser) {
        Friendship friendship = friendshipRepository.findBetween(user, otherUser).orElseThrow(NotFoundException::new);

        friendship.reject(user);
        friendshipRepository.save(friendship);
    }

    public void removeFriend(UserId user, UUID otherUserId) {
        UserId otherUser = UserId.from(otherUserId);
        Friendship friendship = friendshipRepository.findBetween(user, otherUser).orElseThrow(NotFoundException::new);

        if (friendship.status() != FriendshipStatus.ACCEPTED)
            throw new CannotRemoveException();

        friendshipRepository.delete(friendship);
    }

    public void cancelRequest(UserId user, UUID otherUserId) {
        UserId otherUser = UserId.from(otherUserId);
        Friendship friendship = friendshipRepository.findBetween(user, otherUser).orElseThrow(NotFoundException::new);

        friendship.cancel(user);
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