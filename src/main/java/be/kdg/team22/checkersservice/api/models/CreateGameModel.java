package be.kdg.team22.checkersservice.api.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateGameModel(
        boolean hasBot,
        @NotEmpty List<UUID> players,
        @NotNull GameSettingsModel settings
) {
}