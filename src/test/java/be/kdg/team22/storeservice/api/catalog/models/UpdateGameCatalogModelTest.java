package be.kdg.team22.storeservice.api.catalog.models;

import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UpdateGameCatalogModelTest {

    @Test
    void validModel_shouldHoldValues() {
        UpdateEntryModel model =
                new UpdateEntryModel(BigDecimal.valueOf(12), GameCategory.CARD);

        assertThat(model.price()).isEqualTo(BigDecimal.valueOf(12));
        assertThat(model.category()).isEqualTo(GameCategory.CARD);
    }
}