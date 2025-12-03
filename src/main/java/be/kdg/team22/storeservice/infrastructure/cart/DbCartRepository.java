package be.kdg.team22.storeservice.infrastructure.cart;

import be.kdg.team22.storeservice.domain.cart.Cart;
import be.kdg.team22.storeservice.domain.cart.CartRepository;
import be.kdg.team22.storeservice.infrastructure.cart.jpa.CartEntity;
import be.kdg.team22.storeservice.infrastructure.cart.jpa.JpaCartRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class DbCartRepository implements CartRepository {

    private final JpaCartRepository jpa;

    public DbCartRepository(JpaCartRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Cart> findByUserId(final UUID userId) {
        return jpa.findByUserId(userId)
                .map(CartEntity::toDomain);
    }

    @Override
    public Cart save(final Cart cart) {
        jpa.save(CartEntity.fromDomain(cart));
        return cart;
    }
}