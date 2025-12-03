package be.kdg.team22.storeservice.infrastructure.cart.jpa;

import be.kdg.team22.storeservice.domain.cart.CartItem;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "cart_item")
public class CartItemEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID gameId;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_user_id", nullable = false)
    private CartEntity cart;

    protected CartItemEntity() {
    }

    public CartItemEntity(UUID id, UUID gameId, int quantity, CartEntity cart) {
        this.id = id;
        this.gameId = gameId;
        this.quantity = quantity;
        this.cart = cart;
    }

    public static CartItemEntity fromDomain(CartItem item, CartEntity cart) {
        return new CartItemEntity(
                null,
                item.gameId(),
                item.quantity(),
                cart
        );
    }

    public CartItem toDomain() {
        return new CartItem(gameId, quantity);
    }

    public UUID getId() {
        return id;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public CartEntity getCart() {
        return cart;
    }

    public void setCart(CartEntity cart) {
        this.cart = cart;
    }
}