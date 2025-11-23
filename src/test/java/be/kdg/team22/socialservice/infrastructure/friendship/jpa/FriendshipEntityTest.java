package be.kdg.team22.socialservice.infrastructure.friendship.jpa;

import be.kdg.team22.socialservice.domain.friendship.Friendship;
import be.kdg.team22.socialservice.domain.friendship.FriendshipId;
import be.kdg.team22.socialservice.domain.friendship.FriendshipStatus;
import be.kdg.team22.socialservice.domain.user.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FriendshipEntityTest {
    @Test
    void from_convertsCorrectly() {
        FriendshipId id = FriendshipId.create();
        UserId requester = UserId.from(UUID.randomUUID());
        UserId receiver = UserId.from(UUID.randomUUID());
        Instant created = Instant.now();
        Instant updated = Instant.now();

        Friendship domain = new Friendship(id, requester, receiver, created, updated, FriendshipStatus.PENDING);

        FriendshipEntity entity = FriendshipEntity.from(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.to().id().value()).isEqualTo(id.value());
        assertThat(entity.to().requester().value()).isEqualTo(requester.value());
        assertThat(entity.to().receiver().value()).isEqualTo(receiver.value());
        assertThat(entity.to().status()).isEqualTo(FriendshipStatus.PENDING);
        assertThat(entity.to().createdAt()).isEqualTo(created);
        assertThat(entity.to().updatedAt()).isEqualTo(updated);
    }

    @Test
    void to_convertsCorrectly() {
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

        Friendship domain = entity.to();

        assertThat(domain.id().value()).isEqualTo(id);
        assertThat(domain.requester().value()).isEqualTo(requester);
        assertThat(domain.receiver().value()).isEqualTo(receiver);
        assertThat(domain.status()).isEqualTo(FriendshipStatus.ACCEPTED);
        assertThat(domain.createdAt()).isEqualTo(created);
        assertThat(domain.updatedAt()).isEqualTo(updated);
    }
}