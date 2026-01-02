package be.kdg.team22.communicationservice.api.chat.models.channel;

import be.kdg.team22.communicationservice.api.chat.models.channel.type.BotReferenceTypeModel;
import be.kdg.team22.communicationservice.api.chat.models.channel.type.LobbyReferenceTypeModel;
import be.kdg.team22.communicationservice.api.chat.models.channel.type.PrivateReferenceTypeModel;
import be.kdg.team22.communicationservice.api.chat.models.channel.type.ReferenceTypeModel;
import be.kdg.team22.communicationservice.api.chat.models.channel.type.exceptions.ConvertReferenceTypeException;
import be.kdg.team22.communicationservice.domain.chat.channel.Channel;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.BotReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.LobbyReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.PrivateReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.ReferenceType;

import java.util.UUID;

public record ChannelModel(UUID id,
                           ChannelType type,
                           ReferenceTypeModel referenceType) {
    public static ChannelModel from(Channel channel) {
        ReferenceTypeModel referenceType = getReferenceType(channel.referenceType());
        return new ChannelModel(channel.id().value(), channel.referenceType().type(), referenceType);
    }

    private static ReferenceTypeModel getReferenceType(final ReferenceType referenceType) {
        return switch (referenceType) {
            case BotReferenceType botReferenceType ->
                    new BotReferenceTypeModel(botReferenceType.userId().value());
            case LobbyReferenceType lobbyReferenceType ->
                    new LobbyReferenceTypeModel(lobbyReferenceType.lobbyId().value());
            case PrivateReferenceType privateReferenceType ->
                    new PrivateReferenceTypeModel(privateReferenceType.userId().value(), privateReferenceType.otherUserId().value());
            default ->
                    throw new ConvertReferenceTypeException();
        };
    }
}