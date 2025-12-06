package be.kdg.team22.communicationservice.api.notification;

import be.kdg.team22.communicationservice.api.notification.models.NotificationModel;
import be.kdg.team22.communicationservice.application.notification.NotificationService;
import be.kdg.team22.communicationservice.domain.notification.NotificationId;
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

    public NotificationController(NotificationService service) {
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<List<NotificationModel>> getAll(
            @AuthenticationPrincipal Jwt jwt) {

        PlayerId playerId = PlayerId.get(jwt);

        List<NotificationModel> result = service.getNotifications(playerId)
                .stream()
                .map(NotificationModel::from)
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationModel>> getUnread(
            @AuthenticationPrincipal Jwt jwt) {

        PlayerId playerId = PlayerId.get(jwt);

        List<NotificationModel> result = service.getUnreadNotifications(playerId)
                .stream()
                .map(NotificationModel::from)
                .toList();

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {

        service.markAsRead(NotificationId.from(id));

        return ResponseEntity.ok().build();
    }
}