package be.kdg.team22.gameaclservice.api.model;

import java.util.List;
import java.util.UUID;

public record CreateChessGameModel(
        List<UUID> players
) {
}
