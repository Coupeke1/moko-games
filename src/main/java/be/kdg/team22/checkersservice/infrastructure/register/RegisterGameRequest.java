package be.kdg.team22.checkersservice.infrastructure.register;

import java.math.BigDecimal;

public record RegisterGameRequest(
        String name,
        String backendUrl,
        String frontendUrl,
        String startEndpoint,
        String healthEndpoint,

        String title,
        String description,
        String image,
        BigDecimal price,
        String category
) {
}
