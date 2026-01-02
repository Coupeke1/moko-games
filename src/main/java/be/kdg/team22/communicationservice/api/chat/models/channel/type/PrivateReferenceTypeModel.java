package be.kdg.team22.communicationservice.api.chat.models.channel.type;

import java.util.UUID;

public record PrivateReferenceTypeModel(
        UUID userId,
        UUID otherUserId) implements ReferenceTypeModel {}