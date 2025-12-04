package be.kdg.team22.storeservice.api.order.models;

import be.kdg.team22.storeservice.domain.order.Order;
import be.kdg.team22.storeservice.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderResponseModel(
        UUID id,
        BigDecimal totalPrice,
        OrderStatus status,
        List<OrderItemModel> items
) {
    public static OrderResponseModel from(final Order order) {
        return new OrderResponseModel(
                order.id().value(),
                order.totalPrice(),
                order.status(),
                order.items().stream()
                        .map(OrderItemModel::from)
                        .toList()
        );
    }
}