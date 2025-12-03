package be.kdg.team22.storeservice.infrastructure.cart.jpa;

import be.kdg.team22.storeservice.domain.cart.Cart;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "cart")
public class CartEntity {

    @Id
    private UUID userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemEntity> items = new ArrayList<>();

    protected CartEntity() {
    }

    public CartEntity(UUID userId, List<CartItemEntity> items) {
        this.userId = userId;
        this.items = items;
    }

    public static CartEntity fromDomain(Cart domain) {
        CartEntity entity = new CartEntity();
        entity.userId = domain.userId();

        entity.items = domain.items().stream()
                .map(item -> CartItemEntity.fromDomain(item, entity))
                .collect(Collectors.toList());
        return entity;
    }

    public Cart toDomain() {
        Cart cart = new Cart(this.userId);

        this.items.forEach(CartItemEntity::toDomain);

        return cart;
    }

    public UUID getUserId() {
        return userId;
    }

    public List<CartItemEntity> getItems() {
        return items;
    }

    public void setItems(List<CartItemEntity> items) {
        this.items = items;
    }
}