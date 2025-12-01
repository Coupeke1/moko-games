package be.kdg.team22.sessionservice.api.lobby.models;

import be.kdg.team22.sessionservice.domain.player.Player;

import java.util.UUID;

public record PlayerSummaryModel(UUID id,
                                 String username,
                                 String image) {
    public static PlayerSummaryModel from(Player player) {
        return new PlayerSummaryModel(player.id().value(), player.username().value(), player.image());
    }
}
