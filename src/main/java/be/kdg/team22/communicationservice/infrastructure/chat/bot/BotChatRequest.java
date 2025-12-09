package be.kdg.team22.communicationservice.infrastructure.chat.bot;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BotChatRequest(
        String question,
        @JsonProperty("team_number") int teamNumber,
        @JsonProperty("game_name") String gameName
) {
}