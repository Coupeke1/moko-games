package be.kdg.team22.socialservice.friends.api.models;

import java.util.List;

public record FriendsOverviewModel(
        List<FriendModel> friends,
        List<FriendModel> incoming,
        List<FriendModel> outgoing
) { }
