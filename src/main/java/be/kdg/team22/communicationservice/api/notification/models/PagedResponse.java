package be.kdg.team22.communicationservice.api.notification.models;

import java.util.List;

public record PagedResponse<T>(List<T> items, int page, int size, boolean last) {
}