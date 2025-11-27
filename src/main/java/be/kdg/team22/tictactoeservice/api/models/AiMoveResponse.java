package be.kdg.team22.tictactoeservice.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiMoveResponse(
        @JsonProperty("game_name") String gameName,
        @JsonProperty("game_status") String gameStatus,
        int row,
        int col
) {
}
