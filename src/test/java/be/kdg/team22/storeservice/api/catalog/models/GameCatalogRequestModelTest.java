package be.kdg.team22.storeservice.api.catalog.models;

import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class GameCatalogRequestModelTest {

    @Test
    void validModel_shouldHoldValues() {
        UUID id = UUID.randomUUID();
        GameCatalogRequestModel model = new GameCatalogRequestModel(id, BigDecimal.TEN, GameCategory.CARD);

        assertThat(model.id()).isEqualTo(id);
        assertThat(model.price()).isEqualTo(BigDecimal.TEN);
        assertThat(model.category()).isEqualTo(GameCategory.CARD);
    }
}
