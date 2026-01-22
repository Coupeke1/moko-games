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
    private final JpaCartRepository repository;

    public DbCartRepository(JpaCartRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Cart> findByUserId(final UUID userId) {
        return repository.findByUserId(userId)
                .map(CartEntity::to);
    }

    @Override
    public Cart save(final Cart cart) {
        repository.save(CartEntity.from(cart));
        return cart;
    }
}