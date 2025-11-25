package be.kdg.team22.gamesservice.domain.game.exceptions;

import java.util.UUID;

public class EngineGameNotFoundException extends RuntimeException {
    public EngineGameNotFoundException(UUID templateId) {
        super("Engine did not recognize game template '%s'".formatted(templateId));
    }
}