package be.kdg.team22.communicationservice.domain.chat.ai;

import java.util.Objects;

public record AIResponse(String model, String answer) {

    public AIResponse(String model, String answer) {
        this.model = Objects.requireNonNull(model);
        this.answer = Objects.requireNonNull(answer);
    }
}
