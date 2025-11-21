package be.kdg.team22.sessionservice.infrastructure.lobby.db.friends;

import java.util.List;

public record FriendsOverviewResponse(
        List<FriendsResponse> friends,
        List<FriendsResponse> incoming,
        List<FriendsResponse> outgoing
) {
}