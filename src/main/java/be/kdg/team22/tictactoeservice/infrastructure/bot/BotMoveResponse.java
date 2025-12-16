package be.kdg.team22.tictactoeservice.infrastructure.bot;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BotMoveResponse(
        @JsonProperty("game_name") String gameName,
        @JsonProperty("game_status") String gameStatus,
        @JsonProperty("row") int row,
        @JsonProperty("col") int col
) {
}
