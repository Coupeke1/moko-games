package be.kdg.team22.gamesservice.api.game.models;

import java.util.UUID;

public record StartGameResponseModel(
        UUID gameInstanceId
) {
}