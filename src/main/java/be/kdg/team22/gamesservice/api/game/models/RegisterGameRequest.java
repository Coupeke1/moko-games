package be.kdg.team22.gamesservice.api.game.models;

public record RegisterGameRequest(
        String name,
        String backendUrl,
        String frontendUrl,
        String startEndpoint,

        String title,
        String description,
        String image
) {
}
