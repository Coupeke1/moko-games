package be.kdg.team22.socialservice.friends.infrastructure.db.repositories;

import be.kdg.team22.socialservice.friends.domain.Friendship;
import be.kdg.team22.socialservice.friends.domain.FriendshipId;
import be.kdg.team22.socialservice.friends.domain.UserId;
import be.kdg.team22.socialservice.friends.infrastructure.db.entities.FriendshipEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FriendshipRepositoryImplTest {

    private final FriendshipJpaRepository jpa = mock(FriendshipJpaRepository.class);
    private final FriendshipRepositoryImpl repo = new FriendshipRepositoryImpl(jpa);

    private FriendshipEntity entity(UUID id, UUID requester, UUID receiver) {
        return new FriendshipEntity(
                id,
                requester,
                receiver,
                be.kdg.team22.socialservice.friends.domain.FriendshipStatus.PENDING,
                Instant.now(),
                Instant.now()
        );
    }

    private Friendship domain(UUID id, UUID requester, UUID receiver) {
        return entity(id, requester, receiver).toDomain();
    }

    @Test
    void findById_mapsEntityToDomain() {
        UUID id = UUID.randomUUID();
        UUID req = UUID.randomUUID();
        UUID rec = UUID.randomUUID();

        when(jpa.findById(id)).thenReturn(Optional.of(entity(id, req, rec)));

        Optional<Friendship> result = repo.findById(new FriendshipId(id));

        assertThat(result).isPresent();
        assertThat(result.get().id().value()).isEqualTo(id);
        assertThat(result.get().requester().value()).isEqualTo(req);
        assertThat(result.get().receiver().value()).isEqualTo(rec);
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        UUID id = UUID.randomUUID();

        when(jpa.findById(id)).thenReturn(Optional.empty());

        Optional<Friendship> result = repo.findById(new FriendshipId(id));

        assertThat(result).isEmpty();
    }

    @Test
    void findBetween_returnsDomainModel() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();

        when(jpa.findBetween(a, b)).thenReturn(Optional.of(entity(UUID.randomUUID(), a, b)));

        Optional<Friendship> result =
                repo.findBetween(UserId.from(a), UserId.from(b));

        assertThat(result).isPresent();
        assertThat(result.get().requester().value()).isEqualTo(a);
        assertThat(result.get().receiver().value()).isEqualTo(b);
    }

    @Test
    void findBetween_returnsEmptyWhenNoneFound() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();

        when(jpa.findBetween(a, b)).thenReturn(Optional.empty());

        Optional<Friendship> result =
                repo.findBetween(UserId.from(a), UserId.from(b));

        assertThat(result).isEmpty();
    }

    @Test
    void findAllFor_mapsAllEntitiesToDomain() {
        UUID user = UUID.randomUUID();
        UUID other = UUID.randomUUID();

        FriendshipEntity e1 = entity(UUID.randomUUID(), user, other);
        FriendshipEntity e2 = entity(UUID.randomUUID(), other, user);

        when(jpa.findByRequesterIdOrReceiverId(user, user))
                .thenReturn(List.of(e1, e2));

        List<Friendship> result = repo.findAllFor(UserId.from(user));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).requester().value()).isIn(user, other);
        assertThat(result.get(1).requester().value()).isIn(user, other);
    }

    @Test
    void save_convertsDomainToEntity() {
        Friendship domain = domain(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        repo.save(domain);

        verify(jpa).save(any(FriendshipEntity.class));
    }

    @Test
    void delete_callsJpaDeleteById() {
        UUID id = UUID.randomUUID();
        Friendship domain = domain(
                id,
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        repo.delete(domain);

        verify(jpa).deleteById(id);
    }
}
