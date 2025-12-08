package be.kdg.team22.communicationservice.domain.notification.exceptions;
import be.kdg.team22.communicationservice.domain.notification.NotificationId;
public class EmailSendingException extends RuntimeException {
    public EmailSendingException(String recipientEmail, NotificationId notificationId, Throwable cause) {
        super("Failed to send email to " + recipientEmail + " for notification " + notificationId.value(), cause);
    }
}
