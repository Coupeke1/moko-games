package be.kdg.team22.storeservice.infrastructure.catalog.jpa;

import be.kdg.team22.storeservice.domain.catalog.Entry;
import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import be.kdg.team22.storeservice.domain.catalog.GameId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntryEntityTest {

    private Entry sampleDomain() {
        return new Entry(GameId.create(), BigDecimal.valueOf(19.99), GameCategory.CARD, 7, Collections.emptyList());
    }

    @Test
    @DisplayName("from maps all fields correctly")
    void from_mapsAllFields() {
        Entry domain = sampleDomain();

        EntryEntity entity = EntryEntity.from(domain);

        assertThat(entity.id()).isEqualTo(domain.id().value());
        assertThat(entity.price()).isEqualTo(domain.price());
        assertThat(entity.category()).isEqualTo(domain.category());
        assertThat(entity.purchaseCount()).isEqualTo(domain.purchaseCount());
    }

    @Test
    @DisplayName("to maps entity fields back to a domain object")
    void to_mapsBackCorrectly() {
        UUID id = UUID.randomUUID();
        BigDecimal price = BigDecimal.TEN;
        GameCategory category = GameCategory.STRATEGY;
        int purchaseCount = 4;

        EntryEntity entity = new EntryEntity(id, price, category, purchaseCount, Collections.emptyList());

        Entry domain = entity.to();

        assertThat(domain.id().value()).isEqualTo(id);
        assertThat(domain.price()).isEqualTo(price);
        assertThat(domain.category()).isEqualTo(category);
        assertThat(domain.purchaseCount()).isEqualTo(purchaseCount);
    }

    @Test
    @DisplayName("Mapping domain → entity → domain retains all data")
    void roundTripMapping_preservesAllData() {
        Entry original = sampleDomain();

        EntryEntity entity = EntryEntity.from(original);
        Entry result = entity.to();

        assertThat(result.id()).isEqualTo(original.id());
        assertThat(result.price()).isEqualTo(original.price());
        assertThat(result.category()).isEqualTo(original.category());
        assertThat(result.purchaseCount()).isEqualTo(original.purchaseCount());
    }

    @Test
    @DisplayName("Constructor assigns all fields correctly")
    void constructor_assignsFields() {
        UUID id = UUID.randomUUID();
        BigDecimal price = BigDecimal.valueOf(5);
        GameCategory category = GameCategory.FAMILY;
        int purchaseCount = 9;

        EntryEntity entity = new EntryEntity(id, price, category, purchaseCount, Collections.emptyList());

        assertThat(entity.id()).isEqualTo(id);
        assertThat(entity.price()).isEqualTo(price);
        assertThat(entity.category()).isEqualTo(category);
        assertThat(entity.purchaseCount()).isEqualTo(purchaseCount);
    }

    @Test
    @DisplayName("Setters correctly update mutable fields")
    void setters_updateFieldsCorrectly() {
        UUID id = UUID.randomUUID();
        EntryEntity entity = new EntryEntity(id, BigDecimal.ONE, GameCategory.CARD, 2, Collections.emptyList());

        entity.setPrice(BigDecimal.TEN);
        entity.setCategory(GameCategory.PARTY);
        entity.setPurchaseCount(42);

        assertThat(entity.price()).isEqualTo(BigDecimal.TEN);
        assertThat(entity.category()).isEqualTo(GameCategory.PARTY);
        assertThat(entity.purchaseCount()).isEqualTo(42);
    }

    @Test
    @DisplayName("Protected no-arg constructor creates an empty shell")
    void jpaConstructor_createsEmptyShell() {
        EntryEntity entity = new EntryEntity();

        assertThat(entity.id()).isNull();
        assertThat(entity.price()).isNull();
        assertThat(entity.category()).isNull();
        assertThat(entity.purchaseCount()).isEqualTo(0);
    }
}