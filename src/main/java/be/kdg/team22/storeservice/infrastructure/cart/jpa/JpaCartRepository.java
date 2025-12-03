package be.kdg.team22.storeservice.infrastructure.cart.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaCartRepository extends JpaRepository<CartEntity, UUID> {
}