package be.kdg.team22.communicationservice.api.chat.models.channel;

import be.kdg.team22.communicationservice.api.chat.models.channel.type.ReferenceTypeModel;
import be.kdg.team22.communicationservice.domain.chat.channel.Channel;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelType;

import java.util.UUID;

public record ChannelModel(UUID id,
                           ChannelType type,
                           ReferenceTypeModel referenceType) {
    public static ChannelModel from(final Channel channel, final ReferenceTypeModel referenceType) {
        return new ChannelModel(channel.id().value(), channel.referenceType().type(), referenceType);
    }
}