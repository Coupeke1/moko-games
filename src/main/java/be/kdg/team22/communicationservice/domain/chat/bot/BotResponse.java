package be.kdg.team22.communicationservice.domain.chat.bot;

import java.util.Objects;

public record BotResponse(String model, String answer) {
    public BotResponse(String model, String answer) {
        this.model = Objects.requireNonNull(model);
        this.answer = Objects.requireNonNull(answer);
    }
}
