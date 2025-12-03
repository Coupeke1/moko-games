package be.kdg.team22.storeservice.infrastructure.user;

import java.util.UUID;

public record LibraryGameResponse(UUID id, String title, boolean favourite) {
}