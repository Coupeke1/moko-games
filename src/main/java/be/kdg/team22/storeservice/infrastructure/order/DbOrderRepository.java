package be.kdg.team22.storeservice.infrastructure.order;

import be.kdg.team22.storeservice.domain.order.Order;
import be.kdg.team22.storeservice.domain.order.OrderRepository;
import be.kdg.team22.storeservice.infrastructure.order.jpa.JpaOrderRepository;
import be.kdg.team22.storeservice.infrastructure.order.jpa.OrderEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class DbOrderRepository implements OrderRepository {
    private final JpaOrderRepository repository;

    public DbOrderRepository(final JpaOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(final Order order) {
        repository.save(OrderEntity.fromDomain(order));
    }

    @Override
    public Optional<Order> findById(final UUID id) {
        return repository.findById(id).map(OrderEntity::toDomain);
    }

    @Override
    public Optional<Order> findByPaymentId(final String paymentId) {
        return repository.findByPaymentId(paymentId).map(OrderEntity::toDomain);
    }
}
