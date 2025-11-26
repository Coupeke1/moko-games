package be.kdg.team22.gamesservice.api.game.models;

import java.util.List;

public record GameListModel(
        List<GameDetailsModel> games
) {
}