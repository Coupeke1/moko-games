package be.kdg.team22.storeservice.infrastructure.cart.jpa;

import be.kdg.team22.storeservice.domain.cart.CartItem;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "cart_items")
public class CartItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID gameId;

    private int quantity;

    protected CartItemEntity() {
    }

    public CartItemEntity(UUID gameId, int quantity) {
        this.gameId = gameId;
        this.quantity = quantity;
    }

    public static CartItemEntity fromDomain(CartItem item) {
        return new CartItemEntity(item.gameId(), item.quantity());
    }

    public CartItem toDomain() {
        return new CartItem(gameId, quantity);
    }

    public void setCart(CartEntity cart) {
    }
}