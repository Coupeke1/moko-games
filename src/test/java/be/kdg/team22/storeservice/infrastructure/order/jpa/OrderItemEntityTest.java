package be.kdg.team22.storeservice.infrastructure.order.jpa;

import be.kdg.team22.storeservice.domain.catalog.GameId;
import be.kdg.team22.storeservice.domain.order.OrderItem;
import be.kdg.team22.storeservice.domain.order.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemEntityTest {

    @Test
    @DisplayName("from maps OrderItem → OrderItemEntity correctly")
    void from_mapsCorrectly() {
        UUID gameId = UUID.randomUUID();
        BigDecimal price = BigDecimal.valueOf(12.50);

        OrderItem domain = new OrderItem(GameId.from(gameId), price);

        OrderItemEntity entity = OrderItemEntity.from(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.to().gameId().value()).isEqualTo(gameId);
        assertThat(entity.to().price()).isEqualByComparingTo(price);
    }

    @Test
    @DisplayName("to maps OrderItemEntity → OrderItem correctly")
    void to_mapsCorrectly() {
        UUID gameId = UUID.randomUUID();
        BigDecimal price = BigDecimal.valueOf(19.99);

        OrderItemEntity entity = new OrderItemEntity(gameId, price);

        OrderItem domain = entity.to();

        assertThat(domain.gameId().value()).isEqualTo(gameId);
        assertThat(domain.price()).isEqualByComparingTo(price);
    }

    @Test
    @DisplayName("setOrder correctly links OrderItemEntity to its parent OrderEntity")
    void setOrder_linksCorrectly() {
        UUID orderId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        BigDecimal price = BigDecimal.TEN;

        OrderItemEntity itemEntity = new OrderItemEntity(gameId, price);

        new OrderEntity(
                orderId,
                userId,
                "paymentId",
                OrderStatus.PENDING_PAYMENT,
                BigDecimal.valueOf(10),
                java.util.List.of(itemEntity)
        );

        assertThat(itemEntity).isNotNull();
        assertThat(itemEntity.to().gameId().value()).isEqualTo(gameId);
        assertThat(itemEntity.to().price()).isEqualByComparingTo(price);

        assertThat(itemEntity.to()).isNotNull();
        assertThat(itemEntity.to().gameId().value()).isEqualTo(gameId);
    }
}