package be.kdg.team22.userservice.infrastructure.library.jpa;

import be.kdg.team22.userservice.domain.library.LibraryEntry;
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
    @DisplayName("fromDomain → maps domain to entity correctly")
    void fromDomain_mapsCorrectly() {
        LibraryEntry domain = new LibraryEntry(id, userId, gameId, purchasedAt);

        LibraryEntryEntity entity = LibraryEntryEntity.fromDomain(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.id()).isEqualTo(id);
        assertThat(entity.userId()).isEqualTo(userId);
        assertThat(entity.gameId()).isEqualTo(gameId);
        assertThat(entity.purchasedAt()).isEqualTo(purchasedAt);
    }

    @Test
    @DisplayName("toDomain → maps entity back to domain correctly")
    void toDomain_mapsCorrectly() {
        LibraryEntryEntity entity =
                new LibraryEntryEntity(id, userId, gameId, purchasedAt);

        LibraryEntry domain = entity.toDomain();

        assertThat(domain.id()).isEqualTo(id);
        assertThat(domain.userId()).isEqualTo(userId);
        assertThat(domain.gameId()).isEqualTo(gameId);
        assertThat(domain.purchasedAt()).isEqualTo(purchasedAt);
    }

    @Test
    @DisplayName("Roundtrip: domain → entity → domain is consistent")
    void roundTrip_isConsistent() {
        LibraryEntry original = new LibraryEntry(id, userId, gameId, purchasedAt);

        LibraryEntryEntity entity = LibraryEntryEntity.fromDomain(original);
        LibraryEntry mapped = entity.toDomain();

        assertThat(mapped).isEqualTo(original);
        assertThat(mapped.id()).isEqualTo(original.id());
        assertThat(mapped.userId()).isEqualTo(original.userId());
        assertThat(mapped.gameId()).isEqualTo(original.gameId());
        assertThat(mapped.purchasedAt()).isEqualTo(original.purchasedAt());
    }

    @Test
    @DisplayName("Entity constructor sets all fields correctly")
    void constructor_setsFieldsCorrectly() {
        LibraryEntryEntity entity =
                new LibraryEntryEntity(id, userId, gameId, purchasedAt);

        assertThat(entity.id()).isEqualTo(id);
        assertThat(entity.userId()).isEqualTo(userId);
        assertThat(entity.gameId()).isEqualTo(gameId);
        assertThat(entity.purchasedAt()).isEqualTo(purchasedAt);
    }
}