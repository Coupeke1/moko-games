package be.kdg.team22.storeservice.api.catalog.models;

import java.util.List;

public record PagedResponse<T>(
        List<T> items,
        int page,
        int size,
        boolean last
) {}
