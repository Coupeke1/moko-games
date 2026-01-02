package be.kdg.team22.gameaclservice.infrastructure.games;

import be.kdg.team22.gameaclservice.config.ChessInfoProperties;
import be.kdg.team22.gameaclservice.events.inbound.ChessRegisterEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
        String category,

        List<RegisterAchievementRequest> achievements
) {
    public static RegisterGameRequest convert(ChessRegisterEvent event, ChessInfoProperties chessInfo, String aclBackendUrl) {
        String frontendUrl = event.frontendUrl();
        String baseFrontendUrl = frontendUrl.replaceAll("/[a-f0-9\\-]+/?$", "");

        List<RegisterAchievementRequest> achievements = new ArrayList<>();
        event.availableAchievements().forEach(achievement -> achievements.add(RegisterAchievementRequest.convert(achievement)));

        return new RegisterGameRequest(
                "chess",
                aclBackendUrl,
                baseFrontendUrl,
                "/api/games/chess",
                "/actuator/health",
                "Chess",
                "Play chess against other players online.",
                event.pictureUrl(),
                chessInfo.price(),
                chessInfo.category(),
                achievements
        );
    }
}
