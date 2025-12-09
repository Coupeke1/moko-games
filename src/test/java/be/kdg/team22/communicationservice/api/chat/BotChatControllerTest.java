package be.kdg.team22.communicationservice.api.chat;

import be.kdg.team22.communicationservice.application.chat.BotChatService;
import be.kdg.team22.communicationservice.config.SecurityConfig;
import be.kdg.team22.communicationservice.domain.chat.*;
import be.kdg.team22.communicationservice.domain.chat.exceptions.NotBotChannelOwnerException;
import be.kdg.team22.communicationservice.utils.TestJwtUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BotChatController.class)
@Import(SecurityConfig.class)
class BotChatControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    BotChatService botChatService;

    // ==================== Authentication Tests ====================

    @Test
    void createBotChannel_withoutToken_returnsUnauthorized() throws Exception {
        mvc.perform(post("/api/chat/bot"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sendMessage_withoutToken_returnsUnauthorized() throws Exception {
        mvc.perform(post("/api/chat/bot/" + UUID.randomUUID() + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content": "Hello", "gameName": "GO"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMessages_withoutToken_returnsUnauthorized() throws Exception {
        mvc.perform(get("/api/chat/bot/" + UUID.randomUUID() + "/messages"))
                .andExpect(status().isUnauthorized());
    }

    // ==================== Authorization Tests ====================

    @Test
    void sendMessage_notOwner_returnsForbidden() throws Exception {
        UUID channelId = UUID.randomUUID();

        Mockito.when(botChatService.sendMessage(
                eq(channelId),
                eq("otherUser"),
                any(),
                any()
        )).thenThrow(new NotBotChannelOwnerException("otherUser", channelId.toString()));

        mvc.perform(post("/api/chat/bot/" + channelId + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content": "Hello", "gameName": "GO"}
                                """)
                        .with(jwt().jwt(TestJwtUtils.jwtFor("otherUser")))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void getMessages_notOwner_returnsForbidden() throws Exception {
        UUID channelId = UUID.randomUUID();

        Mockito.when(botChatService.getMessages(
                eq(channelId),
                eq("otherUser"),
                isNull()
        )).thenThrow(new NotBotChannelOwnerException("otherUser", channelId.toString()));

        mvc.perform(get("/api/chat/bot/" + channelId + "/messages")
                        .with(jwt().jwt(TestJwtUtils.jwtFor("otherUser")))
                )
                .andExpect(status().isForbidden());
    }

    // ==================== Happy Path Tests ====================

    @Test
    void createBotChannel_returnsChannelResponse() throws Exception {
        ChatChannelId channelId = ChatChannelId.create();
        Channel channel = new Channel(channelId, ChatChannelType.BOT, "user123");

        Mockito.when(botChatService.createChannel("user123")).thenReturn(channel);

        mvc.perform(post("/api/chat/bot")
                        .with(jwt().jwt(TestJwtUtils.jwtFor("user123")))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(channelId.value().toString()))
                .andExpect(jsonPath("$.type").value("BOT"))
                .andExpect(jsonPath("$.referenceId").value("user123"));
    }

    @Test
    void sendMessage_returnsChatMessageResponse() throws Exception {
        ChatChannelId channelId = ChatChannelId.create();
        ChatMessageId msgId = ChatMessageId.create();

        ChatMessage msg = new ChatMessage(
                msgId,
                channelId,
                "bot:bot",
                "Hello! I can help you with GO rules.",
                Instant.now()
        );

        Mockito.when(botChatService.sendMessage(
                eq(channelId.value()),
                eq("user123"),
                eq("What are the rules?"),
                eq("GO")
        )).thenReturn(msg);

        mvc.perform(post("/api/chat/bot/" + channelId.value() + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content": "What are the rules?", "gameName": "GO"}
                                """)
                        .with(jwt().jwt(TestJwtUtils.jwtFor("user123")))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.senderId").value("bot:bot"))
                .andExpect(jsonPath("$.content").value("Hello! I can help you with GO rules."));
    }

    @Test
    void getMessages_returnsList() throws Exception {
        ChatChannelId channelId = ChatChannelId.create();

        ChatMessage msg = new ChatMessage(
                ChatMessageId.create(),
                channelId,
                "user123",
                "Hi!",
                Instant.now()
        );

        Mockito.when(botChatService.getMessages(
                eq(channelId.value()),
                eq("user123"),
                isNull()
        )).thenReturn(List.of(msg));

        mvc.perform(get("/api/chat/bot/" + channelId.value() + "/messages")
                        .with(jwt().jwt(TestJwtUtils.jwtFor("user123")))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Hi!"));
    }
}
