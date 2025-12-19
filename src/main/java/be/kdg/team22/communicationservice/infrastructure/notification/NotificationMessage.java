package be.kdg.team22.communicationservice.infrastructure.notification;

import be.kdg.team22.communicationservice.api.notification.models.NotificationModel;

import java.util.UUID;

public record NotificationMessage(UUID userId,
                                  String queue,
                                  NotificationModel payload) {}
