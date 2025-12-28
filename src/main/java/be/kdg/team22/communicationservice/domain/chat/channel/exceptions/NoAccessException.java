package be.kdg.team22.communicationservice.domain.chat.channel.exceptions;

import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelId;

public class NoAccessException extends RuntimeException {
    public NoAccessException(final UserId userId, final ChannelId channelId) {
        super(String.format("Channel with id '%s' is not accessible by user with id '%s'", userId.value(), channelId.value()));
    }
}