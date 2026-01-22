package be.kdg.team22.tictactoeservice.infrastructure.bot;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record BotMoveRequest(
        @JsonProperty("game_id") String gameId,
        @JsonProperty("game_name") String gameName,
        @JsonProperty("board") List<List<String>> board,
        @JsonProperty("current_player") String currentPlayer,
        @JsonProperty("ai_player") String botPlayer
) {
}
