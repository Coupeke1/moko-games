package be.kdg.team22.socialservice.infrastructure.friends.friendship;

import be.kdg.team22.socialservice.domain.friendship.Friendship;
import be.kdg.team22.socialservice.domain.friendship.FriendshipId;
import be.kdg.team22.socialservice.domain.friendship.FriendshipRepository;
import be.kdg.team22.socialservice.domain.user.UserId;
import be.kdg.team22.socialservice.infrastructure.friends.friendship.jpa.FriendshipEntity;
import be.kdg.team22.socialservice.infrastructure.friends.friendship.jpa.JpaFriendshipRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DbFriendshipRepository implements FriendshipRepository {
    private final JpaFriendshipRepository repository;

    public DbFriendshipRepository(final JpaFriendshipRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Friendship> findById(final FriendshipId id) {
        return repository.findById(id.value()).map(FriendshipEntity::to);
    }

    @Override
    public Optional<Friendship> findBetween(final UserId u1, final UserId u2) {
        return repository.findBetween(u1.value(), u2.value()).map(FriendshipEntity::to);
    }

    @Override
    public List<Friendship> findAllFor(final UserId user) {
        return repository.findByRequesterIdOrReceiverId(user.value(), user.value()).stream().map(FriendshipEntity::to).toList();
    }

    @Override
    public void save(final Friendship friendship) {
        repository.save(FriendshipEntity.from(friendship));
    }

    @Override
    public void delete(final Friendship friendship) {
        repository.deleteById(friendship.id().value());
    }
}