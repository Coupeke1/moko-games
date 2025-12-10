package be.kdg.team22.checkersservice.infrastructure.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiMoveRequest(
        @JsonProperty("game_id") String gameId,
        @JsonProperty("game_name") String gameName,
        @JsonProperty("board") String[][] board,
        @JsonProperty("current_player") String currentPlayer,
        @JsonProperty("ai_player") String aiPlayer,
        @JsonProperty("king_movement_mode") String kingMovementMode
) {
}
