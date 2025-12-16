package be.kdg.team22.communicationservice.api.notification;

import be.kdg.team22.communicationservice.api.notification.models.NotificationModel;
import be.kdg.team22.communicationservice.api.notification.models.PagedResponse;
import be.kdg.team22.communicationservice.application.notification.NotificationService;
import be.kdg.team22.communicationservice.application.queries.NotificationReadFilter;
import be.kdg.team22.communicationservice.application.queries.PageResult;
import be.kdg.team22.communicationservice.application.queries.Pagination;
import be.kdg.team22.communicationservice.domain.notification.Notification;
import be.kdg.team22.communicationservice.domain.notification.NotificationId;
import be.kdg.team22.communicationservice.domain.notification.NotificationOrigin;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService service;

    public NotificationController(final NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PagedResponse<NotificationModel>> getNotifications(@AuthenticationPrincipal final Jwt token, @RequestParam(required = false) final NotificationReadFilter type, @RequestParam(required = false) final NotificationOrigin origin, @RequestParam(defaultValue = "0") final int page, @RequestParam(defaultValue = "10") final int size) {
        PlayerId playerId = PlayerId.get(token);
        Pagination pagination = new Pagination(page, size);

        PageResult<Notification> result = service.findAllByConstraints(playerId, type, origin, pagination);

        List<NotificationModel> models = result.items().stream().map(NotificationModel::from).toList();

        return ResponseEntity.ok(new PagedResponse<>(models, page, size, result.last()));
    }

    @GetMapping("/since")
    public ResponseEntity<Collection<NotificationModel>> getNotificationsSince(@AuthenticationPrincipal final Jwt token, @RequestParam() final Instant time) {
        PlayerId playerId = PlayerId.get(token);
        List<Notification> notifications = service.getNotificationsSince(playerId, time);
        return ResponseEntity.ok(notifications.stream().map(NotificationModel::from).toList());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable final UUID id) {
        service.markAsRead(NotificationId.from(id));
        return ResponseEntity.ok().build();
    }
}