package be.kdg.team22.storeservice.infrastructure.catalog;

import be.kdg.team22.storeservice.config.TestcontainersConfig;
import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import be.kdg.team22.storeservice.infrastructure.catalog.jpa.EntryEntity;
import be.kdg.team22.storeservice.infrastructure.catalog.jpa.JpaEntryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestcontainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DbEntryRepositoryTest {

    @Autowired
    JpaEntryRepository repository;

    @Test
    @DisplayName("Save and load a GameCatalogEntryEntity")
    void saveAndLoad() {
        UUID id = UUID.randomUUID();

        EntryEntity entity = new EntryEntity(id, BigDecimal.valueOf(14.99), GameCategory.STRATEGY, 3, Collections.emptyList());

        repository.save(entity);
        Optional<EntryEntity> loaded = repository.findById(id);

        assertThat(loaded).isPresent();
        assertThat(loaded.get().id()).isEqualTo(id);
        assertThat(loaded.get().price()).isEqualTo(BigDecimal.valueOf(14.99));
        assertThat(loaded.get().category()).isEqualTo(GameCategory.STRATEGY);
        assertThat(loaded.get().purchaseCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("Update fields and persist changes")
    void updateEntity() {
        UUID id = UUID.randomUUID();

        EntryEntity entity = new EntryEntity(id, BigDecimal.TEN, GameCategory.FAMILY, 1, Collections.emptyList());

        repository.save(entity);

        entity.setPrice(BigDecimal.valueOf(19.99));
        entity.setCategory(GameCategory.CARD);
        entity.setPurchaseCount(7);

        repository.save(entity);

        EntryEntity updated = repository.findById(id).orElseThrow();

        assertThat(updated.price()).isEqualTo(BigDecimal.valueOf(19.99));
        assertThat(updated.category()).isEqualTo(GameCategory.CARD);
        assertThat(updated.purchaseCount()).isEqualTo(7);
    }

    @Test
    @DisplayName("Delete entity removes it from database")
    void deleteEntity() {
        UUID id = UUID.randomUUID();

        EntryEntity entity = new EntryEntity(id, BigDecimal.ONE, GameCategory.ABSTRACT, 0, Collections.emptyList()
        );

        repository.save(entity);
        repository.deleteById(id);

        assertThat(repository.findById(id)).isEmpty();
    }
}