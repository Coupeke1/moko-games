package be.kdg.team22.socialservice.api.friends.models;

import java.util.List;

public record FriendsOverviewModel(
        List<FriendModel> friends,
        List<FriendModel> incoming,
        List<FriendModel> outgoing) {}
