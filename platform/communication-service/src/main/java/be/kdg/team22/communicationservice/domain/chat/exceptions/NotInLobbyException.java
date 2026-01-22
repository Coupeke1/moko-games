package be.kdg.team22.communicationservice.domain.chat.exceptions;

import be.kdg.team22.communicationservice.domain.chat.ReferenceId;

import java.util.UUID;

public class NotInLobbyException extends RuntimeException {
    public NotInLobbyException(UUID senderID, UUID referenceId) {
        super("the user with id: " + senderID + " is not in the lobby with referenceId: " + referenceId);
    }
}
