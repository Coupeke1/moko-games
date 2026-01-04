package be.kdg.team22.communicationservice.api.chat.models.channel.type;

import java.util.List;
import java.util.UUID;

public record LobbyReferenceTypeModel(
        UUID lobbyId,
        List<UserModel> players) implements ReferenceTypeModel {}