package be.kdg.team22.storeservice.infrastructure.order.jpa;

import be.kdg.team22.storeservice.domain.cart.UserId;
import be.kdg.team22.storeservice.domain.order.Order;
import be.kdg.team22.storeservice.domain.order.OrderId;
import be.kdg.team22.storeservice.domain.order.OrderStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    private UUID id;

    private UUID userId;
    private String paymentId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<OrderItemEntity> items;

    protected OrderEntity() {
    }

    public OrderEntity(
            final UUID id,
            final UUID userId,
            final String paymentId,
            final OrderStatus status,
            final BigDecimal totalPrice,
            final List<OrderItemEntity> items
    ) {
        this.id = id;
        this.userId = userId;
        this.paymentId = paymentId;
        this.status = status;
        this.totalPrice = totalPrice;
        this.items = items;
        this.items.forEach(i -> i.setOrder(this));
    }

    public static OrderEntity from(final Order order) {
        List<OrderItemEntity> mapped = order.items().stream()
                .map(OrderItemEntity::from)
                .toList();

        return new OrderEntity(
                order.id().value(),
                order.userId().value(),
                order.paymentId(),
                order.status(),
                order.totalPrice(),
                mapped
        );
    }

    public Order to() {
        return new Order(
                OrderId.from(id),
                items.stream().map(OrderItemEntity::to).toList(),
                status,
                UserId.from(userId),
                paymentId
        );
    }
}