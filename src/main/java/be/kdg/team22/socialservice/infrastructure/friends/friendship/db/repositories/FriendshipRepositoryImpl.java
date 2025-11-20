package be.kdg.team22.socialservice.infrastructure.friends.friendship.db.repositories;

import be.kdg.team22.socialservice.domain.friends.friendship.Friendship;
import be.kdg.team22.socialservice.domain.friends.friendship.FriendshipId;
import be.kdg.team22.socialservice.domain.friends.friendship.FriendshipRepository;
import be.kdg.team22.socialservice.domain.friends.user.UserId;
import be.kdg.team22.socialservice.infrastructure.friends.friendship.db.entities.FriendshipEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FriendshipRepositoryImpl implements FriendshipRepository {
    private final FriendshipJpaRepository repository;

    public FriendshipRepositoryImpl(final FriendshipJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Friendship> findById(final FriendshipId id) {
        return repository.findById(id.value()).map(FriendshipEntity::toDomain);
    }

    @Override
    public Optional<Friendship> findBetween(final UserId u1, final UserId u2) {
        return repository.findBetween(u1.value(), u2.value()).map(FriendshipEntity::toDomain);
    }

    @Override
    public List<Friendship> findAllFor(final UserId user) {
        return repository.findByRequesterIdOrReceiverId(user.value(), user.value()).stream().map(FriendshipEntity::toDomain).toList();
    }

    @Override
    public void save(final Friendship friendship) {
        repository.save(FriendshipEntity.fromDomain(friendship));
    }

    @Override
    public void delete(final Friendship friendship) {
        repository.deleteById(friendship.id().value());
    }
}