package be.kdg.team22.storeservice.api.order.models;

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

class OrderResponseModelTest {

    @Test
    @DisplayName("from() maps Order aggregate to OrderResponseModel correctly")
    void from_mapsCorrectly() {
        UUID id = UUID.randomUUID();

        Order order = new Order(
                new OrderId(id),
                List.of(
                        new OrderItem(UUID.randomUUID(), BigDecimal.valueOf(5)),
                        new OrderItem(UUID.randomUUID(), BigDecimal.valueOf(10))
                ),
                OrderStatus.PENDING_PAYMENT
        );

        OrderResponseModel model = OrderResponseModel.from(order);

        assertThat(model.id()).isEqualTo(id);
        assertThat(model.totalPrice()).isEqualTo("15"); // 5 + 10
        assertThat(model.status()).isEqualTo(OrderStatus.PENDING_PAYMENT);
        assertThat(model.items()).hasSize(2);
    }
}