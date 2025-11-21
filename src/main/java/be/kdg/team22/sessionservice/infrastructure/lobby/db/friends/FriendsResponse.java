package be.kdg.team22.sessionservice.infrastructure.lobby.db.friends;

import java.util.List;
import java.util.UUID;

public record FriendsResponse(List<UUID> friendIds) {
}