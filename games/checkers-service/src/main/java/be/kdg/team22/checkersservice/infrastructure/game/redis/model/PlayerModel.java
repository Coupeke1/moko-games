package be.kdg.team22.checkersservice.infrastructure.game.redis.model;

import be.kdg.team22.checkersservice.domain.player.Player;
import be.kdg.team22.checkersservice.domain.player.PlayerId;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;

public class PlayerModel {
    public String id;
    public PlayerRole role;
    public boolean botPlayer;

    public static PlayerModel fromDomain(Player player) {
        PlayerModel playerModel = new PlayerModel();
        playerModel.id = player.id().value().toString();
        playerModel.role = player.role();
        playerModel.botPlayer = player.botPlayer();

        return playerModel;
    }

    public Player toDomain() {
        return new Player(
                PlayerId.create(id),
                role,
                botPlayer
        );
    }
}
