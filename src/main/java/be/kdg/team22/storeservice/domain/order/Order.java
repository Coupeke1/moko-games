package be.kdg.team22.storeservice.domain.order;

import be.kdg.team22.storeservice.domain.order.exceptions.OrderEmptyException;

import java.math.BigDecimal;
import java.util.List;

public class Order {

    private final OrderId id;
    private final List<OrderItem> items;
    private final BigDecimal totalPrice;
    private OrderStatus status;

    public Order(final OrderId id,
                 final List<OrderItem> items,
                 final OrderStatus status) {

        if (items == null || items.isEmpty())
            throw new OrderEmptyException();

        this.id = id;
        this.items = List.copyOf(items);
        this.totalPrice = calculateTotal(items);
        this.status = status;
    }

    private BigDecimal calculateTotal(final List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public OrderId id() {
        return id;
    }

    public List<OrderItem> items() {
        return items;
    }

    public BigDecimal totalPrice() {
        return totalPrice;
    }

    public OrderStatus status() {
        return status;
    }

    public void updateStatus(final OrderStatus newStatus) {
        this.status = newStatus;
    }
}