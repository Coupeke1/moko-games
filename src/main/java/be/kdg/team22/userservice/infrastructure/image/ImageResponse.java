package be.kdg.team22.userservice.infrastructure.image;

public record ImageResponse(String id, String url,
                            int width,
                            int height) {}