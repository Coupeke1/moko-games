package be.kdg.team22.storeservice.api.order.models;

import be.kdg.team22.storeservice.domain.catalog.GameId;
import be.kdg.team22.storeservice.domain.order.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemModelTest {

    @Test
    @DisplayName("from() correctly maps domain to API model")
    void from_mapsCorrectly() {
        UUID gameId = UUID.randomUUID();
        OrderItem item = new OrderItem(GameId.from(gameId), BigDecimal.valueOf(12.50));

        OrderItemModel model = OrderItemModel.from(item);

        assertThat(model.gameId()).isEqualTo(gameId);
        assertThat(model.price()).isEqualTo("12.5");
    }
}