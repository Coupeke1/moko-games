package be.kdg.team22.storeservice.domain.order;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum OrderStatus {
    PENDING_PAYMENT,
    PAID,
    FAILED,
    CANCELED,
    EXPIRED
}