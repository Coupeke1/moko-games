package be.kdg.team22.storeservice.infrastructure.order.jpa;

import be.kdg.team22.storeservice.domain.order.OrderItem;
import be.kdg.team22.storeservice.domain.order.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemEntityTest {

    @Test
    @DisplayName("fromDomain maps OrderItem → OrderItemEntity correctly")
    void fromDomain_mapsCorrectly() {
        UUID gameId = UUID.randomUUID();
        BigDecimal price = BigDecimal.valueOf(12.50);

        OrderItem domain = new OrderItem(gameId, price);

        OrderItemEntity entity = OrderItemEntity.fromDomain(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.toDomain().gameId()).isEqualTo(gameId);
        assertThat(entity.toDomain().price()).isEqualByComparingTo(price);
    }

    @Test
    @DisplayName("toDomain maps OrderItemEntity → OrderItem correctly")
    void toDomain_mapsCorrectly() {
        UUID gameId = UUID.randomUUID();
        BigDecimal price = BigDecimal.valueOf(19.99);

        OrderItemEntity entity = new OrderItemEntity(gameId, price);

        OrderItem domain = entity.toDomain();

        assertThat(domain.gameId()).isEqualTo(gameId);
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
        assertThat(itemEntity.toDomain().gameId()).isEqualTo(gameId);
        assertThat(itemEntity.toDomain().price()).isEqualByComparingTo(price);

        assertThat(itemEntity.toDomain()).isNotNull();
        assertThat(itemEntity.toDomain().gameId()).isEqualTo(gameId);
    }
}