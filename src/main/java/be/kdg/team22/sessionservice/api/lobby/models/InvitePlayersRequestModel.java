package be.kdg.team22.sessionservice.api.lobby.models;

import java.util.List;
import java.util.UUID;

public record InvitePlayersRequestModel(List<UUID> playerIds) {
}
