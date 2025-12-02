package be.kdg.team22.checkersservice.api.models;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record CreateGameModel(
        @NotEmpty List<UUID> players
) {
}