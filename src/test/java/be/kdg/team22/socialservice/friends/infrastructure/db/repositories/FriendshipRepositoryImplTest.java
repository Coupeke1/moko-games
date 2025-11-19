package be.kdg.team22.socialservice.friends.infrastructure.db.repositories;

import be.kdg.team22.socialservice.friends.domain.FriendshipStatus;
import be.kdg.team22.socialservice.friends.infrastructure.db.entities.FriendshipEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FriendshipRepositoryImplTest {

    @Autowired
    private FriendshipJpaRepository jpaRepo;

    @Test
    void saveAndLoadFriendship() {
        UUID id = UUID.randomUUID();
        UUID requester = UUID.randomUUID();
        UUID receiver = UUID.randomUUID();

        FriendshipEntity entity = new FriendshipEntity(
                id,
                requester,
                receiver,
                FriendshipStatus.PENDING,
                Instant.now(),
                Instant.now()
        );

        jpaRepo.save(entity);

        Optional<FriendshipEntity> loaded = jpaRepo.findById(id);

        assertTrue(loaded.isPresent());
        assertEquals(requester, loaded.get().toDomain().requester().value());
        assertEquals(receiver, loaded.get().toDomain().receiver().value());
        assertEquals(FriendshipStatus.PENDING, loaded.get().toDomain().status());
    }

    @Test
    void findBetween_returnsCorrectEntity() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        UUID c = UUID.randomUUID();

        FriendshipEntity f1 = new FriendshipEntity(UUID.randomUUID(), a, b,
                FriendshipStatus.ACCEPTED, Instant.now(), Instant.now());
        FriendshipEntity f2 = new FriendshipEntity(UUID.randomUUID(), c, a,
                FriendshipStatus.PENDING, Instant.now(), Instant.now());

        jpaRepo.save(f1);
        jpaRepo.save(f2);

        Optional<FriendshipEntity> found = jpaRepo.findBetween(a, b);

        assertTrue(found.isPresent());
        assertEquals(a, found.get().toDomain().requester().value());
        assertEquals(b, found.get().toDomain().receiver().value());
    }

    @Test
    void findAllForUser_returnsBothRequesterAndReceiver() {
        UUID user = UUID.randomUUID();
        UUID other1 = UUID.randomUUID();
        UUID other2 = UUID.randomUUID();

        FriendshipEntity f1 = new FriendshipEntity(UUID.randomUUID(), user, other1,
                FriendshipStatus.ACCEPTED, Instant.now(), Instant.now());
        FriendshipEntity f2 = new FriendshipEntity(UUID.randomUUID(), other2, user,
                FriendshipStatus.PENDING, Instant.now(), Instant.now());

        jpaRepo.save(f1);
        jpaRepo.save(f2);

        List<FriendshipEntity> results = jpaRepo.findByRequesterIdOrReceiverId(user, user);

        assertEquals(2, results.size());
    }
}
