package be.kdg.team22.communicationservice.api.chat.models;

import be.kdg.team22.communicationservice.domain.chat.channel.Channel;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelType;

import java.util.UUID;

public record ChannelModel(UUID id,
                           ChannelType type) {
    public static ChannelModel from(Channel channel) {
        return new ChannelModel(channel.id().value(), channel.referenceType().type());
    }
}