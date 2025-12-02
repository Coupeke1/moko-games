package be.kdg.team22.tictactoeservice.infrastructure.game.redis.model;

import be.kdg.team22.tictactoeservice.domain.game.Move;
import be.kdg.team22.tictactoeservice.domain.player.PlayerId;

public class MoveModel {
    public String playerId;
    public int row;
    public int col;

    public static MoveModel fromDomain(Move move){
        MoveModel model = new MoveModel();
        model.playerId = move.playerId().value().toString();
        model.row = move.row();
        model.col = move.col();
        return model;
    }

    public Move toDomain(){
        return new Move(
                PlayerId.create(playerId),
                row,
                col
        );
    }
}
