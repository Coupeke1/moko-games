package be.kdg.team22.storeservice.infrastructure.order.jpa;

import be.kdg.team22.storeservice.domain.catalog.GameId;
import be.kdg.team22.storeservice.domain.order.OrderItem;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItemEntity {

    @Id
    @GeneratedValue
    private Long id;

    private UUID gameId;
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    private OrderEntity order;

    protected OrderItemEntity() {
    }

    public OrderItemEntity(final UUID gameId, final BigDecimal price) {
        this.gameId = gameId;
        this.price = price;
    }

    static OrderItemEntity from(final OrderItem item) {
        return new OrderItemEntity(item.gameId().value(), item.price());
    }

    OrderItem to() {
        return new OrderItem(GameId.from(gameId), price);
    }

    public void setOrder(final OrderEntity order) {
        this.order = order;
    }
}