package be.kdg.team22.storeservice.api.order.models;

import be.kdg.team22.storeservice.domain.order.OrderItem;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemModel(
        UUID gameId,
        BigDecimal price
) {
    public static OrderItemModel from(final OrderItem item) {
        return new OrderItemModel(
                item.gameId(),
                item.price()
        );
    }
}