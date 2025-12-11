package be.kdg.team22.checkersservice.infrastructure.bot;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BotMoveResponse(
        @JsonProperty("game_name") String gameName,
        @JsonProperty("game_status") String gameStatus,
        @JsonProperty("executed_moves") String executedMoves
) {
}
