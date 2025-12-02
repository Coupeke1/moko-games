package be.kdg.team22.storeservice.api.catalog.models;

import be.kdg.team22.storeservice.domain.catalog.GameCatalogEntry;
import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import be.kdg.team22.storeservice.infrastructure.games.GameMetadataResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class GameCatalogResponseTest {

    @Test
    void from_shouldMapCorrectly() {
        GameCatalogEntry entry = new GameCatalogEntry(
                UUID.randomUUID(),
                BigDecimal.valueOf(20),
                GameCategory.FAMILY,
                3
        );

        GameMetadataResponse meta = new GameMetadataResponse(
                entry.getId(),
                "engine",
                "TicTacToe",
                "desc",
                "img.png",
                Instant.now(),
                Instant.now()
        );

        GameCatalogResponse response = GameCatalogResponse.from(entry, meta);

        assertThat(response.id()).isEqualTo(entry.getId());
        assertThat(response.title()).isEqualTo("TicTacToe");
        assertThat(response.price()).isEqualTo(BigDecimal.valueOf(20));
        assertThat(response.category()).isEqualTo(GameCategory.FAMILY);
        assertThat(response.purchaseCount()).isEqualTo(3);
    }
}