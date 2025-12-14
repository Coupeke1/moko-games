package be.kdg.team22.communicationservice.application.queries;

import java.util.List;

public record PageResult<T>(List<T> items, boolean last) {
}
