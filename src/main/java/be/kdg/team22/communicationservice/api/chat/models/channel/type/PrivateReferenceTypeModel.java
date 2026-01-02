package be.kdg.team22.communicationservice.api.chat.models.channel.type;

public record PrivateReferenceTypeModel(
        UserModel user,
        UserModel otherUser) implements ReferenceTypeModel {}