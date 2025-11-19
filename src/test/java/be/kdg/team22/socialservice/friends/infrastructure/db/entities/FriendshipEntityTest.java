package be.kdg.team22.socialservice.friends.infrastructure.db.entities;

import be.kdg.team22.socialservice.friends.domain.Friendship;
import be.kdg.team22.socialservice.friends.domain.FriendshipId;
import be.kdg.team22.socialservice.friends.domain.FriendshipStatus;
import be.kdg.team22.socialservice.friends.domain.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FriendshipEntityTest {

    @Test
    void fromDomain_convertsCorrectly() {
        FriendshipId id = FriendshipId.newId();
        UserId requester = UserId.from(UUID.randomUUID());
        UserId receiver = UserId.from(UUID.randomUUID());
        Instant created = Instant.now();
        Instant updated = Instant.now();

        Friendship domain = new Friendship(id, requester, receiver, created, updated, FriendshipStatus.PENDING);

        FriendshipEntity entity = FriendshipEntity.fromDomain(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.toDomain().id().value()).isEqualTo(id.value());
        assertThat(entity.toDomain().requester().value()).isEqualTo(requester.value());
        assertThat(entity.toDomain().receiver().value()).isEqualTo(receiver.value());
        assertThat(entity.toDomain().status()).isEqualTo(FriendshipStatus.PENDING);
        assertThat(entity.toDomain().createdAt()).isEqualTo(created);
        assertThat(entity.toDomain().updatedAt()).isEqualTo(updated);
    }

    @Test
    void toDomain_convertsCorrectly() {
        UUID id = UUID.randomUUID();
        UUID requester = UUID.randomUUID();
        UUID receiver = UUID.randomUUID();
        Instant created = Instant.now();
        Instant updated = Instant.now();

        FriendshipEntity entity = new FriendshipEntity(
                id,
                requester,
                receiver,
                FriendshipStatus.ACCEPTED,
                created,
                updated
        );

        Friendship domain = entity.toDomain();

        assertThat(domain.id().value()).isEqualTo(id);
        assertThat(domain.requester().value()).isEqualTo(requester);
        assertThat(domain.receiver().value()).isEqualTo(receiver);
        assertThat(domain.status()).isEqualTo(FriendshipStatus.ACCEPTED);
        assertThat(domain.createdAt()).isEqualTo(created);
        assertThat(domain.updatedAt()).isEqualTo(updated);
    }
}