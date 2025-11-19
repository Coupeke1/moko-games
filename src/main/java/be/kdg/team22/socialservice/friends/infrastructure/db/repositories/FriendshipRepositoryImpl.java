package be.kdg.team22.socialservice.friends.infrastructure.db.repositories;

import be.kdg.team22.socialservice.friends.domain.Friendship;
import be.kdg.team22.socialservice.friends.domain.FriendshipId;
import be.kdg.team22.socialservice.friends.domain.FriendshipRepository;
import be.kdg.team22.socialservice.friends.domain.UserId;
import be.kdg.team22.socialservice.friends.infrastructure.db.entities.FriendshipEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FriendshipRepositoryImpl implements FriendshipRepository {

    private final FriendshipJpaRepository jpa;

    public FriendshipRepositoryImpl(FriendshipJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Friendship> findById(FriendshipId id) {
        return jpa.findById(id.value()).map(FriendshipEntity::toDomain);
    }

    @Override
    public Optional<Friendship> findBetween(UserId u1, UserId u2) {
        return jpa.findByRequesterIdAndReceiverId(u1.value(), u2.value())
                .or(() -> jpa.findByRequesterIdAndReceiverId(u2.value(), u1.value()))
                .map(FriendshipEntity::toDomain);
    }

    @Override
    public List<Friendship> findAllFor(UserId user) {
        return jpa.findByRequesterIdOrReceiverId(user.value(), user.value())
                .stream()
                .map(FriendshipEntity::toDomain)
                .toList();
    }

    @Override
    public void save(Friendship friendship) {
        jpa.save(FriendshipEntity.fromDomain(friendship));
    }

    @Override
    public void delete(Friendship friendship) {
        jpa.deleteById(friendship.id().value());
    }
}
