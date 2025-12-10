package be.kdg.team22.checkersservice.infrastructure.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiMoveResponse(
        @JsonProperty("game_name") String gameName,
        @JsonProperty("game_status") String gameStatus,
        @JsonProperty("executed_moves") String executedMoves
) {
}
