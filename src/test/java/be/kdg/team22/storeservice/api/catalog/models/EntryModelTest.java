package be.kdg.team22.storeservice.api.catalog.models;

import be.kdg.team22.storeservice.domain.catalog.GameId;
import be.kdg.team22.storeservice.domain.catalog.Entry;
import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import be.kdg.team22.storeservice.infrastructure.games.GameMetadataResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class EntryModelTest {

    @Test
    void from_shouldMapCorrectly() {
        Entry entry = new Entry(
                GameId.create(),
                BigDecimal.valueOf(20),
                GameCategory.FAMILY,
                3,
                Collections.emptyList()
        );

        GameMetadataResponse meta = new GameMetadataResponse(
                entry.id().value(),
                "engine",
                "TicTacToe",
                "desc",
                "img.png",
                Instant.now(),
                Instant.now()
        );

        EntryModel response = EntryModel.from(entry, meta);

        assertThat(response.id()).isEqualTo(entry.id().value());
        assertThat(response.title()).isEqualTo("TicTacToe");
        assertThat(response.price()).isEqualTo(BigDecimal.valueOf(20));
        assertThat(response.category()).isEqualTo(GameCategory.FAMILY);
        assertThat(response.purchaseCount()).isEqualTo(3);
    }
}