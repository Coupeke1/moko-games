package be.kdg.team22.sessionservice.infrastructure.friends;

import java.util.List;

public record FriendsOverviewResponse(
        List<FriendsResponse> friends,
        List<FriendsResponse> incoming,
        List<FriendsResponse> outgoing
) {
}