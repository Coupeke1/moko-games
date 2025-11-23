package be.kdg.team22.socialservice.api.friends.models;

import java.util.UUID;

public record FriendModel(UUID id,
                          String username,
                          String status) {}
