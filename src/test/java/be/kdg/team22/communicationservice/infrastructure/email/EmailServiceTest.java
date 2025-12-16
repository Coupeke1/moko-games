package be.kdg.team22.communicationservice.infrastructure.email;

import be.kdg.team22.communicationservice.domain.notification.Notification;
import be.kdg.team22.communicationservice.domain.notification.NotificationId;
import be.kdg.team22.communicationservice.domain.notification.NotificationOrigin;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import be.kdg.team22.communicationservice.domain.notification.exceptions.EmailSendingException;
import be.kdg.team22.communicationservice.domain.notification.exceptions.EmailTemplateException;
import jakarta.mail.Address;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    private static final String FROM_EMAIL = "noreply@example.com";
    private JavaMailSender mailSender;
    private TemplateEngine templateEngine;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        templateEngine = mock(TemplateEngine.class);
        emailService = new EmailService(mailSender, templateEngine, FROM_EMAIL);
    }

    @Test
    void sendNotificationEmail_shouldSendEmailWithCorrectDetails() {
        // Given
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("email/notification"), any(Context.class)))
                .thenReturn("<html>Test Email</html>");

        Notification notification = createNotification(NotificationOrigin.FRIEND_REQUEST_RECEIVED);

        // When
        emailService.sendNotificationEmail("test@example.com", "Test User", notification);

        // Then
        verify(mailSender).send(mimeMessage);
        verify(templateEngine).process(eq("email/notification"), any(Context.class));
    }

    @Test
    void sendNotificationEmail_shouldSetCorrectTemplateVariables() {
        // Given
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        when(templateEngine.process(eq("email/notification"), contextCaptor.capture()))
                .thenReturn("<html>Test</html>");

        Notification notification = new Notification(
                NotificationId.from(UUID.randomUUID()),
                PlayerId.from(UUID.randomUUID()),
                NotificationOrigin.ACHIEVEMENT_UNLOCKED,
                "Achievement Unlocked!",
                "You earned the 'First Win' achievement",
                Instant.now(),
                false
        );

        // When
        emailService.sendNotificationEmail("test@example.com", "Jane Doe", notification);

        // Then
        Context context = contextCaptor.getValue();
        assertThat(context.getVariable("recipientName")).isEqualTo("Jane Doe");
        assertThat(context.getVariable("title")).isEqualTo("Achievement Unlocked!");
        assertThat(context.getVariable("message")).isEqualTo("You earned the 'First Win' achievement");
        assertThat(context.getVariable("notificationType")).isEqualTo("ACHIEVEMENT_UNLOCKED");
        assertThat(context.getVariable("color")).isEqualTo("#F59E0B");
    }

    @Test
    void sendNotificationEmail_shouldThrowEmailSendingExceptionOnMailFailure() throws Exception {
        // Given
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenReturn("<html>Test</html>");


        doThrow(jakarta.mail.MessagingException.class)
                .when(mimeMessage).setFrom((Address) any());

        Notification notification = createNotification(NotificationOrigin.DIRECT_MESSAGE);

        // When / Then
        assertThatThrownBy(() ->
                emailService.sendNotificationEmail("test@example.com", "User", notification)
        )
                .isInstanceOf(EmailSendingException.class)
                .hasMessageContaining("test@example.com");
    }


    @Test
    void sendNotificationEmail_shouldThrowEmailTemplateExceptionOnTemplateError() {
        // Given
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenThrow(new RuntimeException("Template not found"));

        Notification notification = createNotification(NotificationOrigin.ORDER_COMPLETED);

        // When / Then
        assertThatThrownBy(() ->
                emailService.sendNotificationEmail("test@example.com", "User", notification)
        )
                .isInstanceOf(EmailTemplateException.class)
                .hasMessageContaining("test@example.com");
    }

    @Test
    void getColorForType_shouldReturnCorrectColorForFriendRequest() {
        testColorForType(NotificationOrigin.FRIEND_REQUEST_RECEIVED, "#4F46E5");
        testColorForType(NotificationOrigin.FRIEND_REQUEST_ACCEPTED, "#4F46E5");
    }

    @Test
    void getColorForType_shouldReturnCorrectColorForLobby() {
        testColorForType(NotificationOrigin.LOBBY_INVITE, "#7C3AED");
        testColorForType(NotificationOrigin.PLAYER_JOINED_LOBBY, "#7C3AED");
    }

    @Test
    void getColorForType_shouldReturnCorrectColorForAchievement() {
        testColorForType(NotificationOrigin.ACHIEVEMENT_UNLOCKED, "#F59E0B");
    }

    @Test
    void getColorForType_shouldReturnCorrectColorForOrder() {
        testColorForType(NotificationOrigin.ORDER_COMPLETED, "#10B981");
    }

    @Test
    void getColorForType_shouldReturnCorrectColorForMessage() {
        testColorForType(NotificationOrigin.DIRECT_MESSAGE, "#3B82F6");
    }

    private void testColorForType(NotificationOrigin type, String expectedColor) {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        when(templateEngine.process(eq("email/notification"), contextCaptor.capture()))
                .thenReturn("<html>Test</html>");

        Notification notification = createNotification(type);

        emailService.sendNotificationEmail("test@example.com", "User", notification);

        Context context = contextCaptor.getValue();
        assertThat(context.getVariable("color")).isEqualTo(expectedColor);
    }

    private Notification createNotification(NotificationOrigin type) {
        return new Notification(
                NotificationId.from(UUID.randomUUID()),
                PlayerId.from(UUID.randomUUID()),
                type,
                "Test Title",
                "Test message",
                Instant.now(),
                false
        );
    }
}