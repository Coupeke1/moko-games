package be.kdg.team22.storeservice.domain.catalog.exceptions;

import java.math.BigDecimal;

public class GameCatalogException extends RuntimeException {
    public GameCatalogException(String message) {
        super(message);
    }

    public static GameCatalogException PriceMustBePositive(BigDecimal price) {
        return new GameCatalogException("Price must be positive, but was: " + price);
    }
}
