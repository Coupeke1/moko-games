package be.kdg.team22.communicationservice.api.notification;

import be.kdg.team22.communicationservice.api.notification.models.NotificationModel;
import be.kdg.team22.communicationservice.application.notification.NotificationService;
import be.kdg.team22.communicationservice.domain.notification.Notification;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class NotificationSocketController {
    private final NotificationService service;

    public NotificationSocketController(final NotificationService service) {
        this.service = service;
    }

    @SubscribeMapping("/notifications")
    public List<NotificationModel> handleSubscription(final Jwt token) {
        PlayerId playerId = PlayerId.get(token);
        List<Notification> notifications = service.getNotifications(playerId);
        return notifications.stream().map(NotificationModel::from).toList();
    }
}