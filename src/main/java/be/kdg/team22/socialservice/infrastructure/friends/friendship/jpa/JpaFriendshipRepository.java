package be.kdg.team22.socialservice.infrastructure.friends.friendship.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaFriendshipRepository extends JpaRepository<FriendshipEntity, UUID> {
    @Query("""
                SELECT f FROM FriendshipEntity f
                WHERE (f.requesterId = :u1 AND f.receiverId = :u2)
                   OR (f.requesterId = :u2 AND f.receiverId = :u1)
            """)
    Optional<FriendshipEntity> findBetween(@Param("u1") UUID u1, @Param("u2") UUID u2);

    List<FriendshipEntity> findByRequesterIdOrReceiverId(UUID requesterId, UUID receiverId);
}