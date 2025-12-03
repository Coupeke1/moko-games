package be.kdg.team22.storeservice.infrastructure.cart.jpa;

import be.kdg.team22.storeservice.domain.cart.Cart;
import be.kdg.team22.storeservice.domain.cart.CartId;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "carts")
public class CartEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemEntity> items;

    protected CartEntity() {
    }

    public CartEntity(UUID id, UUID userId, List<CartItemEntity> items) {
        this.id = id;
        this.userId = userId;
        this.items = items;
        this.items.forEach(i -> i.setCart(this));
    }

    public static CartEntity fromDomain(Cart cart) {
        var itemEntities = cart.items().stream()
                .map(CartItemEntity::fromDomain)
                .toList();

        return new CartEntity(
                cart.id().value(),
                cart.userId(),
                itemEntities
        );
    }

    public Cart toDomain() {
        return new Cart(
                CartId.from(id),
                userId,
                items.stream().map(CartItemEntity::toDomain).toList()
        );
    }
}