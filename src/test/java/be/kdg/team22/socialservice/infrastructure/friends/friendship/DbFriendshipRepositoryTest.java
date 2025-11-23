package be.kdg.team22.socialservice.infrastructure.friends.friendship;

import be.kdg.team22.socialservice.config.TestcontainersConfig;
import be.kdg.team22.socialservice.domain.friendship.Friendship;
import be.kdg.team22.socialservice.domain.friendship.FriendshipId;
import be.kdg.team22.socialservice.domain.friendship.FriendshipStatus;
import be.kdg.team22.socialservice.domain.user.UserId;
import be.kdg.team22.socialservice.infrastructure.friends.friendship.jpa.FriendshipEntity;
import be.kdg.team22.socialservice.infrastructure.friends.friendship.jpa.JpaFriendshipRepository;
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
class DbFriendshipRepositoryTest {
    @Autowired
    private JpaFriendshipRepository jpa;
    private DbFriendshipRepository repo;

    @BeforeEach
    void setup() {
        repo = new DbFriendshipRepository(jpa);
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
        ).to();

        repo.save(toSave);

        Optional<FriendshipEntity> db = jpa.findById(id);

        assertThat(db).isPresent();
        assertThat(db.get().to().id().value()).isEqualTo(id);
    }

    @Test
    void delete_removesEntity() {
        UUID id = UUID.randomUUID();
        FriendshipEntity e = entity(id,
                UUID.randomUUID(),
                UUID.randomUUID());
        jpa.save(e);

        repo.delete(e.to());

        assertThat(jpa.findById(id)).isEmpty();
    }
}
