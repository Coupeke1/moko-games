package be.kdg.team22.tictactoeservice.events;

import be.kdg.team22.tictactoeservice.domain.game.Game;
import be.kdg.team22.tictactoeservice.domain.player.PlayerRole;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AiMoveRequestedEvent(
        @JsonProperty("game_id") String gameId,
        @JsonProperty("game_name") String gameName,
        List<List<String>> board,
        @JsonProperty("current_player") PlayerRole currentPlayer,
        @JsonProperty("ai_player") PlayerRole aiPlayer
) {
    public static AiMoveRequestedEvent from(Game game) {
        return new AiMoveRequestedEvent(
                game.id().value().toString(),
                "TICTACTOE",
                game.board().boardState(),
                game.currentRole(),
                game.aiPlayer()
        );
    }
}
