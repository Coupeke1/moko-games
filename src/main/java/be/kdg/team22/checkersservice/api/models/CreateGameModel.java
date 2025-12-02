package be.kdg.team22.checkersservice.api.models;

import java.util.List;
import java.util.UUID;

public record CreateGameModel(
        List<UUID> players
) {
}