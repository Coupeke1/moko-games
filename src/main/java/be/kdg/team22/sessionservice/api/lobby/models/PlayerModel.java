package be.kdg.team22.sessionservice.api.lobby.models;

import be.kdg.team22.sessionservice.domain.player.Player;

import java.util.UUID;

public record PlayerModel(UUID id,
                          String username,
                          String image,
                          boolean ready) {
    public static PlayerModel from(Player player) {
        return new PlayerModel(player.id().value(), player.username().value(), player.image(), player.ready());
    }
}
