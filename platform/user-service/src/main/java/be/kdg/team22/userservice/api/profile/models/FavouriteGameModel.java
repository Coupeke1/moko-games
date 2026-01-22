package be.kdg.team22.userservice.api.profile.models;

import java.util.UUID;

public record FavouriteGameModel(
        UUID id,
        String title,
        String description,
        String image
) {
}