package be.kdg.team22.communicationservice.domain.notification.exceptions;

public class EmailTemplateException extends RuntimeException {
    public EmailTemplateException(String recipientEmail, Throwable cause) {
        super("Failed to process email template for " + recipientEmail, cause);
    }
}

