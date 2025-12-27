package be.kdg.team22.communicationservice.domain.chat.exceptions;

import be.kdg.team22.communicationservice.domain.chat.channel.ChannelType;

public class ChatChannelNotFoundException extends RuntimeException {
    public ChatChannelNotFoundException(ChannelType type, String referenceId) {
        super("Cant find chat channel with origin: " + type + " and referenceId: " + referenceId);
    }
}
