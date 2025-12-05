package be.kdg.team22.communicationservice.domain.chat.exceptions;

import be.kdg.team22.communicationservice.domain.chat.ChatChannelType;

public class ChatChannelNotFoundException extends RuntimeException {
    public ChatChannelNotFoundException(ChatChannelType type, String referenceId) {
        super("Cant find chat channel with type: " + type + " and referenceId: " + referenceId);
    }
}
