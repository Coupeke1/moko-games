package be.kdg.team22.communicationservice.api.chat.models.channel.type;

import java.util.UUID;

public record BotReferenceTypeModel(
        UUID userId, UUID botId,
        UUID gameId) implements ReferenceTypeModel {}