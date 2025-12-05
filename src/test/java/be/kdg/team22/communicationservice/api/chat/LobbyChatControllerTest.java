package be.kdg.team22.communicationservice.api.chat;

import be.kdg.team22.communicationservice.application.chat.ChatService;
import be.kdg.team22.communicationservice.domain.chat.ChatChannelId;
import be.kdg.team22.communicationservice.domain.chat.ChatChannelType;
import be.kdg.team22.communicationservice.domain.chat.ChatMessage;
import be.kdg.team22.communicationservice.domain.chat.ChatMessageId;
import be.kdg.team22.communicationservice.utils.TestJwtUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LobbyChatController.class)
class LobbyChatControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ChatService service;

    @Test
    void sendLobbyMessage_returnsMessage() throws Exception {
        ChatChannelId channelId = ChatChannelId.create();
        ChatMessage msg = new ChatMessage(
                ChatMessageId.create(),
                channelId,
                "user777",
                "Hello lobby!",
                Instant.now()
        );

        Mockito.when(service.sendMessage(
                eq(ChatChannelType.LOBBY),
                eq("lobby-123"),
                eq("user777"),
                eq("Hello lobby!"),
                any(Jwt.class)
        )).thenReturn(msg);

        mvc.perform(post("/api/chat/lobby/lobby-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content":"Hello lobby!"}
                                """)
                        .with(jwt().jwt(TestJwtUtils.jwtFor("user777")))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.senderId").value("user777"))
                .andExpect(jsonPath("$.content").value("Hello lobby!"));
    }

    @Test
    void getLobbyMessages_returnsList() throws Exception {
        ChatMessage msg = new ChatMessage(
                ChatMessageId.create(),
                ChatChannelId.create(),
                "user777",
                "Lobby msg!",
                Instant.now()
        );

        Mockito.when(service.getMessages(
                eq(ChatChannelType.LOBBY),
                eq("lobby-123"),
                isNull(),
                eq("user777"),
                any(Jwt.class)
        )).thenReturn(List.of(msg));

        mvc.perform(get("/api/chat/lobby/lobby-123")
                        .with(jwt().jwt(TestJwtUtils.jwtFor("user777")))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Lobby msg!"));
    }
}
