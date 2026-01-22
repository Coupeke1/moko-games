package be.kdg.team22.socialservice.domain.friendship;

import be.kdg.team22.socialservice.domain.user.UserId;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository {
    Optional<Friendship> findById(FriendshipId id);

    Optional<Friendship> findBetween(UserId user1, UserId user2);

    List<Friendship> findAllFor(UserId user);

    void save(Friendship friendship);

    void delete(Friendship friendship);
}