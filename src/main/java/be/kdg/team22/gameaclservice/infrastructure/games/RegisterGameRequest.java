package be.kdg.team22.gameaclservice.infrastructure.games;

import be.kdg.team22.gameaclservice.config.ChessInfoProperties;
import be.kdg.team22.gameaclservice.events.inbound.ChessRegisterEvent;
import be.kdg.team22.gameaclservice.events.outbound.GameAchievementEvent;

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
    public static RegisterGameRequest convert(ChessRegisterEvent event, ChessInfoProperties chessInfo) {
        List<RegisterAchievementRequest> achievements = new ArrayList<>();
        event.availableAchievements().forEach(achievement -> achievements.add(RegisterAchievementRequest.convert(achievement)));
        return new RegisterGameRequest(
                "chess",
                chessInfo.backendUrl(),
                event.frontendUrl(),
                "/api/games/chess",
                null,
                "Chess",
                "Play chess against other players online.",
                event.pictureUrl(),
                chessInfo.price(),
                chessInfo.category(),
                achievements
        );
    }
}
