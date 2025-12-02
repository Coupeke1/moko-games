package be.kdg.team22.storeservice.infrastructure.catalog;

import be.kdg.team22.storeservice.config.TestcontainersConfig;
import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import be.kdg.team22.storeservice.infrastructure.catalog.jpa.GameCatalogEntryEntity;
import be.kdg.team22.storeservice.infrastructure.catalog.jpa.JpaGameCatalogEntryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestcontainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DbGameCatalogEntryRepositoryTest {

    @Autowired
    JpaGameCatalogEntryRepository repository;

    @Test
    @DisplayName("Save and load a GameCatalogEntryEntity")
    void saveAndLoad() {
        UUID id = UUID.randomUUID();

        GameCatalogEntryEntity entity = new GameCatalogEntryEntity(
                id,
                BigDecimal.valueOf(14.99),
                GameCategory.STRATEGY,
                3
        );

        repository.save(entity);
        Optional<GameCatalogEntryEntity> loaded = repository.findById(id);

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getId()).isEqualTo(id);
        assertThat(loaded.get().getPrice()).isEqualTo(BigDecimal.valueOf(14.99));
        assertThat(loaded.get().getCategory()).isEqualTo(GameCategory.STRATEGY);
        assertThat(loaded.get().getPurchaseCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("Update fields and persist changes")
    void updateEntity() {
        UUID id = UUID.randomUUID();

        GameCatalogEntryEntity entity = new GameCatalogEntryEntity(
                id, BigDecimal.TEN, GameCategory.FAMILY, 1
        );

        repository.save(entity);

        // update fields
        entity.setPrice(BigDecimal.valueOf(19.99));
        entity.setCategory(GameCategory.CARD);
        entity.setPurchaseCount(7);

        repository.save(entity);

        GameCatalogEntryEntity updated = repository.findById(id).orElseThrow();

        assertThat(updated.getPrice()).isEqualTo(BigDecimal.valueOf(19.99));
        assertThat(updated.getCategory()).isEqualTo(GameCategory.CARD);
        assertThat(updated.getPurchaseCount()).isEqualTo(7);
    }

    @Test
    @DisplayName("Delete entity removes it from database")
    void deleteEntity() {
        UUID id = UUID.randomUUID();

        GameCatalogEntryEntity entity = new GameCatalogEntryEntity(
                id,
                BigDecimal.ONE,
                GameCategory.ABSTRACT,
                0
        );

        repository.save(entity);
        repository.deleteById(id);

        assertThat(repository.findById(id)).isEmpty();
    }
}