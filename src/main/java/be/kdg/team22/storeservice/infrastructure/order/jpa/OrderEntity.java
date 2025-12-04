package be.kdg.team22.storeservice.infrastructure.order.jpa;

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
            final OrderStatus status,
            final BigDecimal totalPrice,
            final List<OrderItemEntity> items
    ) {
        this.id = id;
        this.status = status;
        this.totalPrice = totalPrice;
        this.items = items;
        this.items.forEach(i -> i.setOrder(this));
    }

    public static OrderEntity fromDomain(final Order order) {
        List<OrderItemEntity> mapped = order.items().stream()
                .map(OrderItemEntity::fromDomain)
                .toList();

        return new OrderEntity(
                order.id().value(),
                order.status(),
                order.totalPrice(),
                mapped
        );
    }

    public Order toDomain() {
        return new Order(
                new OrderId(id),
                items.stream().map(OrderItemEntity::toDomain).toList(),
                status
        );
    }
}