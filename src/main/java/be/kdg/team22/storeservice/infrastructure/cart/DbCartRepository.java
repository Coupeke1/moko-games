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
    public Optional<Cart> findByUser(UUID userId) {
        return jpa.findById(userId)
                .map(CartEntity::toDomain);
    }

    @Override
    public void save(Cart cart) {
        jpa.save(CartEntity.fromDomain(cart));
    }

    @Override
    public void delete(UUID userId) {
        jpa.deleteById(userId);
    }
}