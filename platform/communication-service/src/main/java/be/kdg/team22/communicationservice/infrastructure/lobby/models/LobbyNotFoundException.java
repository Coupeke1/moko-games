package be.kdg.team22.communicationservice.infrastructure.lobby.models;

import be.kdg.team22.communicationservice.domain.chat.channel.LobbyId;

public class LobbyNotFoundException extends RuntimeException {
    public LobbyNotFoundException(final LobbyId id) {
        super(String.format("Lobby with id '%s' was not found", id.value()));
    }
}