package be.kdg.team22.storeservice.infrastructure.order.jpa;

import be.kdg.team22.storeservice.domain.order.Order;
import be.kdg.team22.storeservice.domain.order.OrderId;
import be.kdg.team22.storeservice.domain.order.OrderItem;
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
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items;

    protected OrderEntity() {
    }

    public OrderEntity(final UUID id,
                       final OrderStatus status,
                       final BigDecimal totalPrice,
                       final List<OrderItemEntity> items) {

        this.id = id;
        this.status = status;
        this.totalPrice = totalPrice;
        this.items = items;

        this.items.forEach(i -> i.setOrder(this));
    }

    public static OrderEntity fromDomain(final Order order) {
        final List<OrderItemEntity> mappedItems = order.items().stream()
                .map(OrderItemEntity::fromDomain)
                .toList();

        return new OrderEntity(
                order.id().value(),
                order.status(),
                order.totalPrice(),
                mappedItems
        );
    }

    public Order toDomain() {
        final List<OrderItem> domainItems = items.stream()
                .map(OrderItemEntity::toDomain)
                .toList();

        return new Order(
                new OrderId(id),
                domainItems,
                status
        );
    }

    public UUID getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(final OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public List<OrderItemEntity> getItems() {
        return items;
    }
}