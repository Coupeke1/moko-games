package be.kdg.team22.checkersservice.api.models;

import be.kdg.team22.checkersservice.domain.player.Player;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;

import java.util.UUID;

public record PlayerModel(UUID id, PlayerRole role) {

    public static PlayerModel from(Player player) {
        return new PlayerModel(
                player.id().value(),
                player.role()
        );
    }
}