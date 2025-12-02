package be.kdg.team22.storeservice.infrastructure.games;

public record GameMetadataResponse(
        String title,
        String description,
        String thumbnailUrl
) {
}
