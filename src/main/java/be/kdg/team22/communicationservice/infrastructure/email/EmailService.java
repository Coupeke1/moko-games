package be.kdg.team22.communicationservice.infrastructure.email;

import be.kdg.team22.communicationservice.domain.notification.Notification;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final String fromEmail;

    public EmailService(final JavaMailSender mailSender,
                        final TemplateEngine templateEngine,
                        @Value("${spring.mail.username}") final String fromEmail) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.fromEmail = fromEmail;
    }

    @Async
    public void sendNotificationEmail(final String recipientEmail,
                                      final String recipientName,
                                      final Notification notification) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(recipientEmail);
            helper.setSubject(notification.title());

            Context context = new Context();
            context.setVariable("recipientName", recipientName);
            context.setVariable("title", notification.title());
            context.setVariable("message", notification.message());
            context.setVariable("notificationType", notification.type().name());
            context.setVariable("color", getColorForType(notification.type().name()));

            String htmlContent = templateEngine.process("email/notification", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email sent successfully to {} for notification {}", recipientEmail, notification.id().value());

        } catch (MessagingException e) {
            log.error("Failed to send email to {} for notification {}: {}",
                    recipientEmail, notification.id().value(), e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error sending email to {}: {}", recipientEmail, e.getMessage());
        }
    }

    private String getColorForType(String type) {
        return switch (type) {
            case "FRIEND_REQUEST_RECEIVED", "FRIEND_REQUEST_ACCEPTED" -> "#4F46E5";
            case "LOBBY_INVITE", "PLAYER_JOINED_LOBBY" -> "#7C3AED";
            case "ACHIEVEMENT_UNLOCKED" -> "#F59E0B";
            case "ORDER_COMPLETED" -> "#10B981";
            case "DIRECT_MESSAGE" -> "#3B82F6";
            default -> "#6B7280";
        };
    }

}
