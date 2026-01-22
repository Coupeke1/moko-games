package be.kdg.team22.gameaclservice.events.inbound;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ChessRegisterEvent(
    UUID registrationId,
    String frontendUrl,
    String pictureUrl,
    List<ChessAchievement> availableAchievements,
    ChessMessageType messageType,
    LocalDateTime timestamp

) {
    public record ChessAchievement(
            String code,
            String description
    ) {}
}
