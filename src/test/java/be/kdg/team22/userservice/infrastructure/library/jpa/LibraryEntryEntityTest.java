package be.kdg.team22.userservice.infrastructure.library.jpa;

import be.kdg.team22.userservice.domain.library.GameId;
import be.kdg.team22.userservice.domain.library.LibraryEntry;
import be.kdg.team22.userservice.domain.library.LibraryId;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LibraryEntryEntityTest {

    private final UUID id = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private final UUID userId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private final UUID gameId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
    private final Instant purchasedAt = Instant.parse("2024-01-01T10:00:00Z");

    @Test
    @DisplayName("fromDomain → maps ALL fields including favourite")
    void fromDomain_mapsCorrectly() {
        LibraryEntry domain = new LibraryEntry(new LibraryId(id), new ProfileId(userId), new GameId(gameId), purchasedAt, true);

        LibraryEntryEntity entity = LibraryEntryEntity.fromDomain(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.id()).isEqualTo(id);
        assertThat(entity.userId()).isEqualTo(userId);
        assertThat(entity.gameId()).isEqualTo(gameId);
        assertThat(entity.purchasedAt()).isEqualTo(purchasedAt);
        assertThat(entity.favourite()).isTrue();
    }

    @Test
    @DisplayName("toDomain → maps entity back to domain correctly (incl favourite)")
    void toDomain_mapsCorrectly() {
        LibraryEntryEntity entity = new LibraryEntryEntity(id, userId, gameId, purchasedAt, false);

        LibraryEntry domain = entity.toDomain();

        assertThat(domain.id().value()).isEqualTo(id);
        assertThat(domain.userId().value()).isEqualTo(userId);
        assertThat(domain.gameId().value()).isEqualTo(gameId);
        assertThat(domain.purchasedAt()).isEqualTo(purchasedAt);
        assertThat(domain.favourite()).isFalse();
    }

    @Test
    @DisplayName("Roundtrip: domain → entity → domain preserves all fields")
    void roundTrip_isConsistent() {
        LibraryEntry original = new LibraryEntry(new LibraryId(id), new ProfileId(userId), new GameId(gameId), purchasedAt, true);

        LibraryEntryEntity entity = LibraryEntryEntity.fromDomain(original);
        LibraryEntry mapped = entity.toDomain();

        assertThat(mapped).isEqualTo(original);
        assertThat(mapped.favourite()).isTrue();
    }

    @Test
    @DisplayName("Constructor sets all fields including favourite")
    void constructor_setsFieldsCorrectly() {
        LibraryEntryEntity entity = new LibraryEntryEntity(id, userId, gameId, purchasedAt, true);

        assertThat(entity.id()).isEqualTo(id);
        assertThat(entity.userId()).isEqualTo(userId);
        assertThat(entity.gameId()).isEqualTo(gameId);
        assertThat(entity.purchasedAt()).isEqualTo(purchasedAt);
        assertThat(entity.favourite()).isTrue();
    }

    @Test
    @DisplayName("Default favourite = false when no value passed (JPA constructor)")
    void jpaConstructor_defaultFavouriteFalse() {
        LibraryEntryEntity entity = new LibraryEntryEntity();

        assertThat(entity.favourite()).isFalse();
    }
}