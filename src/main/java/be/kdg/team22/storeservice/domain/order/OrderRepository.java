package be.kdg.team22.storeservice.domain.order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    void save(final Order order);

    Optional<Order> findById(final UUID id);
}