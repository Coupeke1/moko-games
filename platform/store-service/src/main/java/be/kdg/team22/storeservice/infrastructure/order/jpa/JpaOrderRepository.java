package be.kdg.team22.storeservice.infrastructure.order.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, UUID> {
    Optional<OrderEntity> findByPaymentId(String paymentId);
}