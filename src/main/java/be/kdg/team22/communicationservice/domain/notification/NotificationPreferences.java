package be.kdg.team22.communicationservice.domain.notification;

public record NotificationPreferences(
        boolean receiveEmail,
        boolean social,
        boolean achievement,
        boolean commerce,
        boolean chat
) {
    public boolean allowsNotification(Notification notification) {
        return switch (notification.type()) {
            case FRIEND_REQUEST_RECEIVED,
                 FRIEND_REQUEST_ACCEPTED,
                 LOBBY_INVITE,
                 PLAYER_JOINED_LOBBY -> social;
            case ACHIEVEMENT_UNLOCKED -> achievement;
            case ORDER_COMPLETED -> commerce;
            case DIRECT_MESSAGE -> chat;
            default -> true;
        };
    }

    public boolean allowsEmail(Notification notification) {
        if (!receiveEmail) return false;
        return allowsNotification(notification);
    }
}