package be.kdg.team22.storeservice.domain.cart;

import org.jmolecules.ddd.annotation.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository {

    Optional<Cart> findByUser(UUID userId);

    void save(Cart cart);

    void delete(UUID userId);
}