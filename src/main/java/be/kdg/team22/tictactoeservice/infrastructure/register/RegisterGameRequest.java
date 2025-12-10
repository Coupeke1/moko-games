package be.kdg.team22.tictactoeservice.infrastructure.register;

public record RegisterGameRequest(
        String name,
        String backendUrl,
        String frontendUrl,
        String startEndpoint,
        String healthEndpoint,

        String title,
        String description,
        String image
) {
}
