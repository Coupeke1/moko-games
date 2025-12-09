package be.kdg.team22.communicationservice.domain.chat.bot;

import java.util.Objects;

public record BotResponse(String answer) {
    public BotResponse(String answer) {
        this.answer = Objects.requireNonNull(answer);
    }
}