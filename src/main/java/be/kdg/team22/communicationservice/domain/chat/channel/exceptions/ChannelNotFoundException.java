package be.kdg.team22.communicationservice.domain.chat.channel.exceptions;

import be.kdg.team22.communicationservice.domain.chat.channel.ChannelId;

public class ChannelNotFoundException extends RuntimeException {
    public ChannelNotFoundException(final ChannelId id) {
        super(String.format("Channel with id '%s' was not found", id.value()));
    }
}