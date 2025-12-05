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

@WebMvcTest(BotChatController.class)
class BotChatControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ChatService service;

    //TODO wanneer AI implementatie er is
    @Test
    void sendMessage_returnsChatMessageResponse() throws Exception {
        ChatChannelId channelId = ChatChannelId.create();
        ChatMessageId msgId = ChatMessageId.create();

        ChatMessage msg = new ChatMessage(
                msgId,
                channelId,
                "user123",
                "Hello bot!",
                Instant.now()
        );

        Mockito.when(service.sendMessage(
                eq(ChatChannelType.BOT),
                eq("ref1"),
                eq("user123"),
                eq("Hello bot!"),
                any(Jwt.class)
        )).thenReturn(msg);

        mvc.perform(post("/api/chat/bot/ref1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content": "Hello bot!"}
                                """)
                        .with(jwt().jwt(TestJwtUtils.jwtFor("user123")))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.senderId").value("user123"))
                .andExpect(jsonPath("$.content").value("Hello bot!"));
    }

    @Test
    void getMessages_returnsList() throws Exception {
        ChatChannelId chId = ChatChannelId.create();

        ChatMessage msg = new ChatMessage(
                ChatMessageId.create(),
                chId,
                "user123",
                "Hi!",
                Instant.now()
        );

        Mockito.when(service.getMessages(
                eq(ChatChannelType.BOT),
                eq("ref1"),
                isNull(),
                eq("user123"),
                any(Jwt.class)
        )).thenReturn(List.of(msg));

        mvc.perform(get("/api/chat/bot/ref1")
                        .with(jwt().jwt(TestJwtUtils.jwtFor("user123")))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Hi!"));
    }
}
