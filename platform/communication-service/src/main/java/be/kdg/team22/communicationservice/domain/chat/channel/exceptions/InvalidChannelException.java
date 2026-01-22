package be.kdg.team22.communicationservice.domain.chat.channel.exceptions;

import be.kdg.team22.communicationservice.domain.chat.channel.ChannelId;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelType;

public class InvalidChannelException extends RuntimeException {
    public InvalidChannelException(final ChannelId id, final ChannelType type) {
        super(String.format("Channel with id '%s' is not %s", id.value(), type));
    }
}