package be.kdg.team22.tictactoeservice.api.models;

import be.kdg.team22.tictactoeservice.domain.player.Player;
import be.kdg.team22.tictactoeservice.domain.player.PlayerRole;

import java.util.UUID;

public record PlayerModel(UUID id, PlayerRole role) {

    public static PlayerModel from(Player player) {
        return new PlayerModel(
                player.id().value(),
                player.role()
        );
    }
}
