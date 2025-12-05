package be.kdg.team22.communicationservice.domain.chat.exceptions;

public class NotInLobbyException extends RuntimeException {
    public NotInLobbyException(String senderID, String referenceId) {
        super("the user with id: " + senderID + " is not in the lobby with referenceId: " + referenceId);
    }
}
