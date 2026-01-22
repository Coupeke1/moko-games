package be.kdg.team22.gameaclservice.api.model;

import be.kdg.team22.gameaclservice.domain.Game;

import java.util.UUID;

public record GameModel(UUID id) {
    public static GameModel from(Game game) {
        return new GameModel(game.id());
    }
}
