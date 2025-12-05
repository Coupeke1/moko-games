package be.kdg.team22.communicationservice.api.chat.models;

import be.kdg.team22.communicationservice.domain.chat.Channel;

import java.util.UUID;

public record ChatChannelResponse(
        UUID id,
        String type,
        String referenceId
) {

    public static ChatChannelResponse from(Channel channel) {
        return new ChatChannelResponse(
                channel.getId().value(),
                channel.getType().name(),
                channel.getReferenceId()
        );
    }
}