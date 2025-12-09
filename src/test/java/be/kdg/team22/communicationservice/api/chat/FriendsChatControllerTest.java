package be.kdg.team22.communicationservice.api.chat;

import be.kdg.team22.communicationservice.application.chat.ChatService;
import be.kdg.team22.communicationservice.domain.chat.ChatChannelId;
import be.kdg.team22.communicationservice.domain.chat.ChatChannelType;
import be.kdg.team22.communicationservice.domain.chat.ChatMessage;
import be.kdg.team22.communicationservice.domain.chat.ChatMessageId;
import be.kdg.team22.communicationservice.domain.chat.exceptions.NotFriendsException;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FriendsChatController.class)
class FriendsChatControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ChatService service;

    private final UUID friendId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    @Test
    void sendFriendMessage_returnsMessage() throws Exception {
        ChatChannelId channelId = ChatChannelId.create();
        ChatMessage msg = new ChatMessage(
                ChatMessageId.create(),
                channelId,
                "user777",
                "Hello friend!",
                Instant.now()
        );

        Mockito.when(service.sendMessage(
                eq(ChatChannelType.FRIENDS),
                eq(friendId.toString()),
                eq("user777"),
                eq("Hello friend!"),
                any(Jwt.class)
        )).thenReturn(msg);

        mvc.perform(post("/api/chat/friends/" + friendId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content":"Hello friend!"}
                                """)
                        .with(jwt().jwt(TestJwtUtils.jwtFor("user777")))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.senderId").value("user777"))
                .andExpect(jsonPath("$.content").value("Hello friend!"));
    }

    @Test
    void sendFriendMessage_notFriends_returnsForbidden() throws Exception {
        Mockito.when(service.sendMessage(
                eq(ChatChannelType.FRIENDS),
                eq(friendId.toString()),
                eq("user777"),
                eq("Hello!"),
                any(Jwt.class)
        )).thenThrow(new NotFriendsException("user777", friendId.toString()));

        mvc.perform(post("/api/chat/friends/" + friendId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content":"Hello!"}
                                """)
                        .with(jwt().jwt(TestJwtUtils.jwtFor("user777")))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void getFriendMessages_returnsList() throws Exception {
        ChatMessage msg = new ChatMessage(
                ChatMessageId.create(),
                ChatChannelId.create(),
                "user777",
                "Friend msg!",
                Instant.now()
        );

        Mockito.when(service.getMessages(
                eq(ChatChannelType.FRIENDS),
                eq(friendId.toString()),
                isNull(),
                eq("user777"),
                any(Jwt.class)
        )).thenReturn(List.of(msg));

        mvc.perform(get("/api/chat/friends/" + friendId)
                        .with(jwt().jwt(TestJwtUtils.jwtFor("user777")))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Friend msg!"));
    }

    @Test
    void getFriendMessages_notFriends_returnsForbidden() throws Exception {
        Mockito.when(service.getMessages(
                eq(ChatChannelType.FRIENDS),
                eq(friendId.toString()),
                isNull(),
                eq("user777"),
                any(Jwt.class)
        )).thenThrow(new NotFriendsException("user777", friendId.toString()));

        mvc.perform(get("/api/chat/friends/" + friendId)
                        .with(jwt().jwt(TestJwtUtils.jwtFor("user777")))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void getFriendMessages_withSince_returnsList() throws Exception {
        Instant since = Instant.parse("2024-01-01T00:00:00Z");
        ChatMessage msg = new ChatMessage(
                ChatMessageId.create(),
                ChatChannelId.create(),
                "user777",
                "Recent msg!",
                Instant.now()
        );

        Mockito.when(service.getMessages(
                eq(ChatChannelType.FRIENDS),
                eq(friendId.toString()),
                eq(since),
                eq("user777"),
                any(Jwt.class)
        )).thenReturn(List.of(msg));

        mvc.perform(get("/api/chat/friends/" + friendId)
                        .param("since", "2024-01-01T00:00:00Z")
                        .with(jwt().jwt(TestJwtUtils.jwtFor("user777")))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Recent msg!"));
    }
}

