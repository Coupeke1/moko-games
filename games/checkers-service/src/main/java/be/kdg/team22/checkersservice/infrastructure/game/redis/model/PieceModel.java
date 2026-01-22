package be.kdg.team22.checkersservice.infrastructure.game.redis.model;

import be.kdg.team22.checkersservice.domain.board.Piece;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;

public class PieceModel {
    public PlayerRole color;
    public boolean isKing;

    public static PieceModel fromDomain(Piece piece) {
        PieceModel pieceModel = new PieceModel();
        pieceModel.color = piece.color();
        pieceModel.isKing = piece.isKing();

        return pieceModel;
    }

    public Piece toDomain() {
        return new Piece(color, isKing);
    }
}
