package be.kdg.team22.communicationservice.api.notification;

import be.kdg.team22.communicationservice.api.notification.models.NotificationModel;
import be.kdg.team22.communicationservice.application.notification.NotificationService;
import be.kdg.team22.communicationservice.domain.notification.NotificationId;
import be.kdg.team22.communicationservice.domain.notification.NotificationType;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService service;

    public NotificationController(final NotificationService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public ResponseEntity<List<NotificationModel>> getMyNotifications(
            @AuthenticationPrincipal final Jwt jwt) {
        PlayerId playerId = PlayerId.get(jwt);

        List<NotificationModel> result = service.getNotifications(playerId)
                .stream()
                .map(NotificationModel::from)
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationModel>> getUnread(
            @AuthenticationPrincipal final Jwt jwt) {
        PlayerId playerId = PlayerId.get(jwt);

        List<NotificationModel> result = service.getUnreadNotifications(playerId)
                .stream()
                .map(NotificationModel::from)
                .toList();

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable final UUID id) {
        service.markAsRead(NotificationId.from(id));

        return ResponseEntity.ok().build();
    }

    //TODO remove this endpoint when we truly add working notifications
    @PostMapping("/test")
    public ResponseEntity<Void> createTest(@AuthenticationPrincipal Jwt jwt) {
        PlayerId id = PlayerId.get(jwt);

        service.create(
                id,
                NotificationType.FRIEND_REQUEST_RECEIVED,
                "Test Social Notification",
                "Dit is een test"
        );

        return ResponseEntity.ok().build();
    }
}