package be.kdg.team22.storeservice.infrastructure.order.jpa;

import be.kdg.team22.storeservice.domain.cart.UserId;
import be.kdg.team22.storeservice.domain.order.Order;
import be.kdg.team22.storeservice.domain.order.OrderId;
import be.kdg.team22.storeservice.domain.order.OrderItem;
import be.kdg.team22.storeservice.domain.order.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderEntityTest {

    @Test
    @DisplayName("fromDomain maps Order → OrderEntity correctly")
    void fromDomain_mapsCorrectly() {
        UUID id = UUID.randomUUID();
        UserId userId = UserId.create();
        Order order = new Order(
                new OrderId(id),
                List.of(
                        new OrderItem(UUID.randomUUID(), BigDecimal.valueOf(5)),
                        new OrderItem(UUID.randomUUID(), BigDecimal.valueOf(10))
                ),
                OrderStatus.PENDING_PAYMENT,
                userId,
                "payment"
        );

        OrderEntity entity = OrderEntity.fromDomain(order);

        assertThat(entity).isNotNull();

        assertThat(entity.toDomain().items()).hasSize(2);
    }

    @Test
    @DisplayName("toDomain maps OrderEntity → Order correctly")
    void toDomain_mapsCorrectly() {
        UUID id = UUID.randomUUID();
        UserId userId = UserId.create();
        OrderItemEntity i1 = new OrderItemEntity(UUID.randomUUID(), BigDecimal.valueOf(5));
        OrderItemEntity i2 = new OrderItemEntity(UUID.randomUUID(), BigDecimal.valueOf(8));

        OrderEntity entity = new OrderEntity(
                id,
                userId.value(),
                "paymentId",
                OrderStatus.PENDING_PAYMENT,
                BigDecimal.valueOf(13),
                List.of(i1, i2)
        );

        Order domain = entity.toDomain();

        assertThat(domain.id().value()).isEqualTo(id);
        assertThat(domain.items()).hasSize(2);
        assertThat(domain.totalPrice()).isEqualByComparingTo("13");
    }

    @Test
    @DisplayName("roundtrip domain → entity → domain produces same values")
    void roundTrip() {
        UUID id = UUID.randomUUID();

        Order original = new Order(
                new OrderId(id),
                List.of(
                        new OrderItem(UUID.randomUUID(), BigDecimal.valueOf(7)),
                        new OrderItem(UUID.randomUUID(), BigDecimal.valueOf(3))
                ),
                OrderStatus.PENDING_PAYMENT,
                UserId.create(),
                "paymentId"
        );

        OrderEntity entity = OrderEntity.fromDomain(original);
        Order mapped = entity.toDomain();

        assertThat(mapped.id().value()).isEqualTo(original.id().value());
        assertThat(mapped.totalPrice()).isEqualTo(original.totalPrice());
        assertThat(mapped.items()).hasSize(2);
        assertThat(mapped.status()).isEqualTo(OrderStatus.PENDING_PAYMENT);
    }
}