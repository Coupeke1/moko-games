package be.kdg.team22.storeservice.infrastructure.catalog.jpa;

import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;
import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GameCatalogEntryEntityTest {

    private GameCatalogEntry sampleDomain() {
        return new GameCatalogEntry(
                UUID.randomUUID(),
                BigDecimal.valueOf(19.99),
                GameCategory.CARD,
                7
        );
    }

    @Test
    @DisplayName("from maps all fields correctly")
    void from_mapsAllFields() {
        GameCatalogEntry domain = sampleDomain();

        GameCatalogEntryEntity entity = GameCatalogEntryEntity.from(domain);

        assertThat(entity.getId()).isEqualTo(domain.getId());
        assertThat(entity.getPrice()).isEqualTo(domain.getPrice());
        assertThat(entity.getCategory()).isEqualTo(domain.getCategory());
        assertThat(entity.getPurchaseCount()).isEqualTo(domain.getPurchaseCount());
    }

    @Test
    @DisplayName("to maps entity fields back to a domain object")
    void to_mapsBackCorrectly() {
        UUID id = UUID.randomUUID();
        BigDecimal price = BigDecimal.TEN;
        GameCategory category = GameCategory.STRATEGY;
        int purchaseCount = 4;

        GameCatalogEntryEntity entity =
                new GameCatalogEntryEntity(id, price, category, purchaseCount);

        GameCatalogEntry domain = entity.to();

        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getPrice()).isEqualTo(price);
        assertThat(domain.getCategory()).isEqualTo(category);
        assertThat(domain.getPurchaseCount()).isEqualTo(purchaseCount);
    }

    @Test
    @DisplayName("Mapping domain → entity → domain retains all data")
    void roundTripMapping_preservesAllData() {
        GameCatalogEntry original = sampleDomain();

        GameCatalogEntryEntity entity = GameCatalogEntryEntity.from(original);
        GameCatalogEntry result = entity.to();

        assertThat(result.getId()).isEqualTo(original.getId());
        assertThat(result.getPrice()).isEqualTo(original.getPrice());
        assertThat(result.getCategory()).isEqualTo(original.getCategory());
        assertThat(result.getPurchaseCount()).isEqualTo(original.getPurchaseCount());
    }

    @Test
    @DisplayName("Constructor assigns all fields correctly")
    void constructor_assignsFields() {
        UUID id = UUID.randomUUID();
        BigDecimal price = BigDecimal.valueOf(5);
        GameCategory category = GameCategory.FAMILY;
        int purchaseCount = 9;

        GameCatalogEntryEntity entity =
                new GameCatalogEntryEntity(id, price, category, purchaseCount);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getPrice()).isEqualTo(price);
        assertThat(entity.getCategory()).isEqualTo(category);
        assertThat(entity.getPurchaseCount()).isEqualTo(purchaseCount);
    }

    @Test
    @DisplayName("Setters correctly update mutable fields")
    void setters_updateFieldsCorrectly() {
        UUID id = UUID.randomUUID();
        GameCatalogEntryEntity entity =
                new GameCatalogEntryEntity(id, BigDecimal.ONE, GameCategory.CARD, 2);

        entity.setPrice(BigDecimal.TEN);
        entity.setCategory(GameCategory.PARTY);
        entity.setPurchaseCount(42);

        assertThat(entity.getPrice()).isEqualTo(BigDecimal.TEN);
        assertThat(entity.getCategory()).isEqualTo(GameCategory.PARTY);
        assertThat(entity.getPurchaseCount()).isEqualTo(42);
    }

    @Test
    @DisplayName("Protected no-arg constructor creates an empty shell")
    void jpaConstructor_createsEmptyShell() {
        GameCatalogEntryEntity entity = new GameCatalogEntryEntity();

        assertThat(entity.getId()).isNull();
        assertThat(entity.getPrice()).isNull();
        assertThat(entity.getCategory()).isNull();
        assertThat(entity.getPurchaseCount()).isEqualTo(0);
    }
}