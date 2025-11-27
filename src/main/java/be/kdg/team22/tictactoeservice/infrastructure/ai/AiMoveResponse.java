package be.kdg.team22.tictactoeservice.infrastructure.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiMoveResponse(
        @JsonProperty("game_name") String gameName,
        @JsonProperty("game_status") String gameStatus,
        @JsonProperty("row") int row,
        @JsonProperty("col") int col
) {
}
