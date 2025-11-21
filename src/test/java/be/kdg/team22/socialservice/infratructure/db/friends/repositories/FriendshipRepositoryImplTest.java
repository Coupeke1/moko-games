package be.kdg.team22.socialservice.infratructure.db.friends.repositories;

import be.kdg.team22.socialservice.config.TestcontainersConfig;
import be.kdg.team22.socialservice.domain.friends.friendship.Friendship;
import be.kdg.team22.socialservice.domain.friends.friendship.FriendshipId;
import be.kdg.team22.socialservice.domain.friends.friendship.FriendshipStatus;
import be.kdg.team22.socialservice.domain.friends.user.UserId;
import be.kdg.team22.socialservice.infrastructure.friends.friendship.db.entities.FriendshipEntity;
import be.kdg.team22.socialservice.infrastructure.friends.friendship.db.repositories.FriendshipJpaRepository;
import be.kdg.team22.socialservice.infrastructure.friends.friendship.db.repositories.FriendshipRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestcontainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FriendshipRepositoryImplTest {

    @Autowired
    private FriendshipJpaRepository jpa;
    private FriendshipRepositoryImpl repo;

    @BeforeEach
    void setup() {
        repo = new FriendshipRepositoryImpl(jpa);
    }

    private FriendshipEntity entity(UUID id, UUID requester, UUID receiver) {
        return new FriendshipEntity(
                id,
                requester,
                receiver,
                FriendshipStatus.PENDING,
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    void findById_mapsEntityToDomain() {
        UUID id = UUID.randomUUID();
        UUID req = UUID.randomUUID();
        UUID rec = UUID.randomUUID();

        FriendshipEntity e = entity(id, req, rec);
        jpa.save(e);

        Optional<Friendship> result = repo.findById(new FriendshipId(id));

        assertThat(result).isPresent();
        assertThat(result.get().requester().value()).isEqualTo(req);
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        Optional<Friendship> result = repo.findById(new FriendshipId(UUID.randomUUID()));

        assertThat(result).isEmpty();
    }

    @Test
    void findBetween_returnsDomainModelWhenPresent() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();

        FriendshipEntity e = entity(UUID.randomUUID(), a, b);
        jpa.save(e);

        Optional<Friendship> result =
                repo.findBetween(UserId.from(a), UserId.from(b));

        assertThat(result).isPresent();
        assertThat(result.get().requester().value()).isEqualTo(a);
        assertThat(result.get().receiver().value()).isEqualTo(b);
    }

    @Test
    void findBetween_returnsEmptyWhenNoneFound() {
        Optional<Friendship> result =
                repo.findBetween(UserId.from(UUID.randomUUID()),
                        UserId.from(UUID.randomUUID()));

        assertThat(result).isEmpty();
    }

    @Test
    void findAllFor_returnsAllFriendshipsForUser() {
        UUID user = UUID.randomUUID();
        UUID other = UUID.randomUUID();

        jpa.save(entity(UUID.randomUUID(), user, other));
        jpa.save(entity(UUID.randomUUID(), other, user));

        List<Friendship> result = repo.findAllFor(UserId.from(user));

        assertThat(result).hasSize(2);
    }

    @Test
    void save_persistsEntity() {
        UUID id = UUID.randomUUID();
        Friendship toSave = entity(id,
                UUID.randomUUID(),
                UUID.randomUUID()
        ).toDomain();

        repo.save(toSave);

        Optional<FriendshipEntity> db = jpa.findById(id);

        assertThat(db).isPresent();
        assertThat(db.get().toDomain().id().value()).isEqualTo(id);
    }

    @Test
    void delete_removesEntity() {
        UUID id = UUID.randomUUID();
        FriendshipEntity e = entity(id,
                UUID.randomUUID(),
                UUID.randomUUID());
        jpa.save(e);

        repo.delete(e.toDomain());

        assertThat(jpa.findById(id)).isEmpty();
    }
}
