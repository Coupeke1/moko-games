package be.kdg.team22.communicationservice.domain.chat.exceptions;

import be.kdg.team22.communicationservice.domain.chat.channel.GameId;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(GameId id) {
        super(String.format("Game with id '%s' was not found", id.value()));
    }
}
