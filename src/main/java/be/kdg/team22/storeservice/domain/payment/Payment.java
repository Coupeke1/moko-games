package be.kdg.team22.storeservice.domain.payment;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record Payment(String id, String checkoutUrl) {
}