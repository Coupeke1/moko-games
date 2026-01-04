package be.kdg.team22.communicationservice.api.chat.models.channel.type;

public record BotReferenceTypeModel(
        UserModel user,
        GameModel game) implements ReferenceTypeModel {}