package be.kdg.team22.communicationservice.api.notification;

import be.kdg.team22.communicationservice.application.notification.NotificationService;
import be.kdg.team22.communicationservice.domain.notification.Notification;
import be.kdg.team22.communicationservice.domain.notification.NotificationId;
import be.kdg.team22.communicationservice.domain.notification.NotificationType;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService service;

    private Jwt jwt;
    private UUID userId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();

        jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", userId.toString())
                .build();
    }

    @Test
    void getMyNotificationsNotifications_returnsMappedModels() throws Exception {
        Notification notification = new Notification(
                NotificationId.from(UUID.randomUUID()),
                PlayerId.from(userId),
                NotificationType.FRIEND_REQUEST_RECEIVED,
                "Test Title",
                "Test Message",
                Instant.parse("2024-01-01T00:00:00Z"),
                false
        );

        Mockito.when(service.getNotifications(eq(PlayerId.from(userId))))
                .thenReturn(List.of(notification));

        mockMvc.perform(
                        get("/api/notifications")
                                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(notification.id().value().toString()))
                .andExpect(jsonPath("$[0].title").value("Test Title"))
                .andExpect(jsonPath("$[0].message").value("Test Message"))
                .andExpect(jsonPath("$[0].type").value("FRIEND_REQUEST_RECEIVED"))
                .andExpect(jsonPath("$[0].read").value(false));
    }

    @Test
    void getUnreadNotifications_returnsUnreadModels() throws Exception {
        Notification notification = new Notification(
                NotificationId.from(UUID.randomUUID()),
                PlayerId.from(userId),
                NotificationType.LOBBY_INVITE,
                "Invite",
                "Join me",
                Instant.parse("2024-01-01T00:00:00Z"),
                false
        );

        Mockito.when(service.getUnreadNotifications(eq(PlayerId.from(userId))))
                .thenReturn(List.of(notification));

        mockMvc.perform(
                        get("/api/notifications/unread")
                                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Invite"))
                .andExpect(jsonPath("$[0].type").value("LOBBY_INVITE"))
                .andExpect(jsonPath("$[0].read").value(false));
    }

    @Test
    void markAsRead_callsServiceAndReturns200() throws Exception {
        UUID notificationId = UUID.randomUUID();

        mockMvc.perform(
                        patch("/api/notifications/" + notificationId + "/read")
                                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
                )
                .andExpect(status().isOk());

        Mockito.verify(service).markAsRead(NotificationId.from(notificationId));
    }
}