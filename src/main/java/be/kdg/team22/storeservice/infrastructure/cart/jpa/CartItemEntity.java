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

    @Column(nullable = false)
    private UUID gameId;

    @ManyToOne(fetch = FetchType.LAZY)
    private CartEntity cart;

    protected CartItemEntity() {
    }

    public CartItemEntity(final UUID gameId) {
        this.gameId = gameId;
    }

    public static CartItemEntity from(final CartItem item) {
        return new CartItemEntity(item.gameId());
    }

    public CartItem to() {
        return new CartItem(gameId);
    }

    public void setCart(CartEntity cart) {
        this.cart = cart;
    }

    public CartEntity cart() {
        return cart;
    }
}