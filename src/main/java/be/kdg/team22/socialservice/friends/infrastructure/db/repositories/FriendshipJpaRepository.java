package be.kdg.team22.socialservice.friends.infrastructure.db.repositories;

import be.kdg.team22.socialservice.friends.infrastructure.db.entities.FriendshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendshipJpaRepository extends JpaRepository<FriendshipEntity, UUID> {

    List<FriendshipEntity> findByRequesterIdOrReceiverId(UUID requesterId, UUID receiverId);

    Optional<FriendshipEntity> findByRequesterIdAndReceiverId(UUID requesterId, UUID receiverId);
}
