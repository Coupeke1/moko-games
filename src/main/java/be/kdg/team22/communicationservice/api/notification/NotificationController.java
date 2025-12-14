package be.kdg.team22.communicationservice.api.notification;

import be.kdg.team22.communicationservice.api.notification.models.NotificationModel;
import be.kdg.team22.communicationservice.application.queries.NotificationReadFilter;
import be.kdg.team22.communicationservice.api.notification.models.PagedResponse;
import be.kdg.team22.communicationservice.application.notification.NotificationService;
import be.kdg.team22.communicationservice.application.queries.Pagination;
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

    @GetMapping
    public ResponseEntity<PagedResponse<NotificationModel>> getNotifications(
            @AuthenticationPrincipal final Jwt jwt,
            @RequestParam(required = false) final NotificationReadFilter type,
            @RequestParam(required = false) final NotificationType origin,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size
    ) {
        PlayerId playerId = PlayerId.get(jwt);

        Pagination pagination = new Pagination(page, size);

        List<NotificationModel> models = service.findAllByConstraints(playerId, type, origin, pagination)
                .stream()
                .map(NotificationModel::from)
                .toList();

        boolean last = models.size() < size;
        return ResponseEntity.ok(new PagedResponse<>(models, page, size, last));
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

    @GetMapping("/read")
    public ResponseEntity<List<NotificationModel>> getRead(
            @AuthenticationPrincipal final Jwt jwt) {
        PlayerId playerId = PlayerId.get(jwt);

        List<NotificationModel> result = service.getReadNotifications(playerId)
                .stream()
                .map(NotificationModel::from)
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<NotificationModel>> getByType(
            @AuthenticationPrincipal final Jwt jwt,
            @PathVariable final NotificationType type) {
        PlayerId playerId = PlayerId.get(jwt);

        List<NotificationModel> result = service.getNotificationsByType(playerId, type)
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