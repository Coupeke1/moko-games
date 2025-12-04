package be.kdg.team22.storeservice.domain.order;

import be.kdg.team22.storeservice.domain.cart.UserId;
import be.kdg.team22.storeservice.domain.order.exceptions.OrderEmptyException;

import java.math.BigDecimal;
import java.util.List;

public class Order {
    private final OrderId id;
    private final List<OrderItem> items;
    private final BigDecimal totalPrice;
    private final UserId userId;
    private OrderStatus status;
    private String paymentId;

    public Order(final OrderId id,
                 final List<OrderItem> items,
                 final OrderStatus status,
                 final UserId userId) {

        if (items == null || items.isEmpty())
            throw new OrderEmptyException();

        this.id = id;
        this.items = List.copyOf(items);
        this.totalPrice = calculateTotal(items);
        this.status = status;
        this.userId = userId;
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

    public UserId userId() {
        return userId;
    }

    public void updateStatus(final OrderStatus status) {
        this.status = status;
    }

    public void attachPaymentId(final String paymentId) {
        this.paymentId = paymentId;
    }

    public String paymentId() {
        return paymentId;
    }
}