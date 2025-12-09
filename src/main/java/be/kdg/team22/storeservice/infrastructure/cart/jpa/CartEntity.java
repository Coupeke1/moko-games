package be.kdg.team22.storeservice.infrastructure.cart.jpa;

import be.kdg.team22.storeservice.domain.cart.Cart;
import be.kdg.team22.storeservice.domain.cart.CartId;
import be.kdg.team22.storeservice.domain.cart.UserId;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "carts")
public class CartEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemEntity> items;

    protected CartEntity() {
    }

    public CartEntity(final UUID id, final UUID userId, final List<CartItemEntity> items) {
        this.id = id;
        this.userId = userId;
        this.items = items;
        this.items.forEach(i -> i.setCart(this));
    }

    public static CartEntity from(final Cart cart) {
        var itemEntities = cart.entries().stream().map(CartItemEntity::from).toList();

        return new CartEntity(cart.id().value(), cart.userId().value(), itemEntities);
    }

    public Cart to() {
        return new Cart(CartId.from(id), UserId.from(userId), items.stream().map(CartItemEntity::to).collect(Collectors.toSet()));
    }

    public UUID id() {
        return id;
    }

    public UUID userId() {
        return userId;
    }

    public List<CartItemEntity> items() {
        return items;
    }
}