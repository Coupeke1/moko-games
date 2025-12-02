package be.kdg.team22.storeservice.application.catalog.queries;

import be.kdg.team22.storeservice.domain.catalog.GameCategory;

import java.util.Optional;

public class FilterQuery {
    public Optional<GameCategory> category = Optional.empty();
    public Optional<Double> minPrice = Optional.empty();
    public Optional<Double> maxPrice = Optional.empty();
    public Optional<String> sortBy = Optional.empty();
}