package be.kdg.team22.storeservice.application.catalog.queries;

import be.kdg.team22.storeservice.domain.catalog.GameCategory;

import java.math.BigDecimal;
import java.util.Optional;

public class FilterQuery {
    public Optional<String> query = Optional.empty();
    public Optional<GameCategory> category = Optional.empty();
    public Optional<BigDecimal> minPrice = Optional.empty();
    public Optional<BigDecimal> maxPrice = Optional.empty();
    public Optional<String> sortBy = Optional.empty();
}