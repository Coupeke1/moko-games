package be.kdg.team22.tictactoeservice.infrastructure.game.redis.model;

import be.kdg.team22.tictactoeservice.domain.player.Player;
import be.kdg.team22.tictactoeservice.domain.player.PlayerId;
import be.kdg.team22.tictactoeservice.domain.player.PlayerRole;

public class PlayerModel {
    public String id;
    public PlayerRole role;
    public boolean bot;

    public static PlayerModel fromDomain(Player player) {
        PlayerModel model = new PlayerModel();
        model.id = player.id().value().toString();
        model.role = player.role();
        model.bot = player.botPlayer();
        return model;
    }

    public Player toDomain() {
        return new Player(
                PlayerId.create(id),
                role,
                bot
        );
    }
}
