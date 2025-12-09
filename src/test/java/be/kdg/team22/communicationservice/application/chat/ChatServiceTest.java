package be.kdg.team22.communicationservice.application.chat;

import be.kdg.team22.communicationservice.domain.chat.*;
import be.kdg.team22.communicationservice.domain.chat.bot.BotChatRepository;
import be.kdg.team22.communicationservice.domain.chat.bot.BotResponse;
import be.kdg.team22.communicationservice.domain.chat.exceptions.CantAutoCreateBotChannel;
import be.kdg.team22.communicationservice.domain.chat.exceptions.ChatChannelNotFoundException;
import be.kdg.team22.communicationservice.domain.chat.exceptions.NotFriendsException;
import be.kdg.team22.communicationservice.domain.chat.exceptions.NotInLobbyException;
import be.kdg.team22.communicationservice.infrastructure.lobby.ExternalSessionRepository;
import be.kdg.team22.communicationservice.infrastructure.messaging.ChatEventPublisher;
import be.kdg.team22.communicationservice.infrastructure.social.ExternalSocialRepository;
import be.kdg.team22.communicationservice.infrastructure.user.ExternalUserRepository;
import be.kdg.team22.communicationservice.infrastructure.user.ProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class ChatServiceTest {

    ChatChannelRepository channelRepo;
    BotChatRepository botRepo;
    ExternalSessionRepository sessionRepo;
    ExternalSocialRepository socialRepo;
    ChatEventPublisher chatEventPublisher;
    ExternalUserRepository userRepo;
    ChatService service;

    Jwt jwt;

    @BeforeEach
    void setup() {
        channelRepo = Mockito.mock(ChatChannelRepository.class);
        botRepo = Mockito.mock(BotChatRepository.class);
        sessionRepo = Mockito.mock(ExternalSessionRepository.class);
        socialRepo = Mockito.mock(ExternalSocialRepository.class);
        chatEventPublisher = Mockito.mock(ChatEventPublisher.class);
        userRepo = Mockito.mock(ExternalUserRepository.class);

        service = new ChatService(channelRepo, botRepo, sessionRepo, socialRepo, chatEventPublisher, userRepo);
        jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.getTokenValue()).thenReturn("jwt-token");

        Mockito.when(userRepo.getProfile(any(UUID.class)))
                .thenReturn(new ProfileResponse(UUID.randomUUID(), "testuser", "test@test.com", null, null));
    }

    @Test
    void botMessage_createsChannelAutomatically_andReturnsAIMessage() {
        String sender = "user123";

        Mockito.when(channelRepo.findByTypeAndReferenceId(eq(ChatChannelType.BOT), eq(sender)))
                .thenReturn(Optional.empty());

        BotResponse botResp = new BotResponse("gpt-mock", "Hello human");
        Mockito.when(botRepo.askBot(any(), any(), any(), any())).thenReturn(botResp);

        Mockito.doAnswer(inv -> null).when(channelRepo).save(any());

        ChatMessage msg = service.sendMessage(ChatChannelType.BOT, "ignored", sender, "Hi bot", jwt);

        assertEquals("bot:gpt-mock", msg.senderId());
        assertEquals("Hello human", msg.content());
        Mockito.verify(botRepo).askBot(any(), eq(sender), eq(sender), eq("Hi bot"));
    }

    @Test
    void botMessage_existingChannelUsed() {
        Channel channel = Channel.createNew(ChatChannelType.BOT, "user123");

        Mockito.when(channelRepo.findByTypeAndReferenceId(eq(ChatChannelType.BOT), eq("user123")))
                .thenReturn(Optional.of(channel));

        Mockito.when(botRepo.askBot(any(), any(), any(), any()))
                .thenReturn(new BotResponse("model1", "response"));

        ChatMessage msg = service.sendMessage(ChatChannelType.BOT, "ignored", "user123", "hello", jwt);

        assertEquals("bot:model1", msg.senderId());
        assertEquals("response", msg.content());
    }

    @Test
    void lobbyMessage_userNotInLobby_throws() {
        Mockito.when(sessionRepo.isPlayerInLobby("user1", "lobby1", "jwt-token"))
                .thenReturn(false);

        assertThrows(NotInLobbyException.class, () ->
                service.sendMessage(ChatChannelType.LOBBY, "lobby1", "user1", "hello", jwt)
        );
    }

    @Test
    void lobbyMessage_createsChannelAndReturnsUserMessage() {
        String lobbyId = "lobby1";

        Mockito.when(sessionRepo.isPlayerInLobby("user1", lobbyId, "jwt-token")).thenReturn(true);
        Mockito.when(channelRepo.findByTypeAndReferenceId(eq(ChatChannelType.LOBBY), eq(lobbyId)))
                .thenReturn(Optional.empty());

        Mockito.doAnswer(inv -> null).when(channelRepo).save(any());

        ChatMessage result = service.sendMessage(ChatChannelType.LOBBY, lobbyId, "user1", "Hello!", jwt);

        assertEquals("user1", result.senderId());
        assertEquals("Hello!", result.content());
        Mockito.verify(botRepo, Mockito.never()).askBot(any(), any(), any(), any());
    }

    @Test
    void sendMessage_blankContent_throwsDomainError() {
        Mockito.when(sessionRepo.isPlayerInLobby(any(), any(), any())).thenReturn(true);
        assertThrows(Exception.class, () ->
                service.sendMessage(ChatChannelType.LOBBY, "lobby1", "u1", "   ", jwt)
        );
    }

    @Test
    void getMessages_bot_overridesReferenceId() {
        Channel channel = Channel.createNew(ChatChannelType.BOT, "user777");

        Mockito.when(channelRepo.findByTypeAndReferenceId(ChatChannelType.BOT, "user777"))
                .thenReturn(Optional.of(channel));

        List<ChatMessage> result = service.getMessages(ChatChannelType.BOT, "ignored", null, "user777", jwt);
        assertNotNull(result);
    }

    @Test
    void getMessages_lobby_notInLobby_throws() {
        Mockito.when(sessionRepo.isPlayerInLobby("u1", "lobbyX", "jwt-token"))
                .thenReturn(false);

        assertThrows(NotInLobbyException.class, () ->
                service.getMessages(ChatChannelType.LOBBY, "lobbyX", null, "u1", jwt)
        );
    }

    @Test
    void getMessages_channelNotFound_throws() {
        Mockito.when(sessionRepo.isPlayerInLobby("u1", "lobby1", "jwt-token")).thenReturn(true);
        Mockito.when(channelRepo.findByTypeAndReferenceId(ChatChannelType.LOBBY, "lobby1"))
                .thenReturn(Optional.empty());

        assertThrows(ChatChannelNotFoundException.class, () ->
                service.getMessages(ChatChannelType.LOBBY, "lobby1", null, "u1", jwt)
        );
    }

    @Test
    void getMessages_sinceNull_returnsAll() {
        Channel channel = Channel.createNew(ChatChannelType.LOBBY, "lobby1");

        channel.postUserMessage("u1", "msg1");
        channel.postUserMessage("u1", "msg2");

        Mockito.when(sessionRepo.isPlayerInLobby("u1", "lobby1", "jwt-token")).thenReturn(true);
        Mockito.when(channelRepo.findByTypeAndReferenceId(ChatChannelType.LOBBY, "lobby1"))
                .thenReturn(Optional.of(channel));

        List<ChatMessage> result = service.getMessages(ChatChannelType.LOBBY, "lobby1", null, "u1", jwt);

        assertEquals(2, result.size());
    }

    @Test
    void getMessages_sinceFiltersCorrectly() throws Exception {
        ChatChannelId channelId = ChatChannelId.create();
        Channel channel = new Channel(channelId, ChatChannelType.LOBBY, "lobby1");

        Instant now = Instant.now();
        Instant oldTime = now.minusSeconds(60);
        Instant newTime = now.minusSeconds(5);

        ChatMessage oldMsg = new ChatMessage(
                ChatMessageId.create(), channelId, "u1", "old", oldTime
        );

        ChatMessage newMsg = new ChatMessage(
                ChatMessageId.create(), channelId, "u1", "new", newTime
        );

        injectMessage(channel, oldMsg);
        injectMessage(channel, newMsg);

        Mockito.when(sessionRepo.isPlayerInLobby("u1", "lobby1", "jwt-token"))
                .thenReturn(true);

        Mockito.when(channelRepo.findByTypeAndReferenceId(ChatChannelType.LOBBY, "lobby1"))
                .thenReturn(Optional.of(channel));

        List<ChatMessage> result =
                service.getMessages(ChatChannelType.LOBBY, "lobby1", now.minusSeconds(10), "u1", jwt);

        assertEquals(1, result.size());
        assertEquals("new", result.getFirst().content());
    }

    @Test
    void createChannel_bot_throws() {
        assertThrows(CantAutoCreateBotChannel.class, () ->
                service.createChannel(ChatChannelType.BOT, "x")
        );
    }

    @Test
    void createChannel_existingReturned() {
        Channel existing = Channel.createNew(ChatChannelType.LOBBY, "l1");

        Mockito.when(channelRepo.findByTypeAndReferenceId(ChatChannelType.LOBBY, "l1"))
                .thenReturn(Optional.of(existing));

        Channel result = service.createChannel(ChatChannelType.LOBBY, "l1");

        assertSame(existing, result);
    }

    @Test
    void createChannel_createsAndSavesNew() {
        Mockito.when(channelRepo.findByTypeAndReferenceId(ChatChannelType.LOBBY, "l1"))
                .thenReturn(Optional.empty());

        Channel created = service.createChannel(ChatChannelType.LOBBY, "l1");

        assertEquals("l1", created.getReferenceId());
        assertEquals(ChatChannelType.LOBBY, created.getType());
        Mockito.verify(channelRepo).save(any());
    }

    @Test
    void friendsMessage_notFriends_throws() {
        String friendId = "550e8400-e29b-41d4-a716-446655440000";
        Mockito.when(socialRepo.areFriends("user1", friendId, "jwt-token"))
                .thenReturn(false);

        assertThrows(NotFriendsException.class, () ->
                service.sendMessage(ChatChannelType.FRIENDS, friendId, "user1", "hello", jwt)
        );
    }

    @Test
    void friendsMessage_areFriends_createsChannelAndReturnsUserMessage() {
        String friendId = "550e8400-e29b-41d4-a716-446655440000";
        String userId = "660e8400-e29b-41d4-a716-446655440000";
        String expectedReferenceId = friendId + "-" + userId; // alphabetically sorted

        Mockito.when(socialRepo.areFriends(userId, friendId, "jwt-token")).thenReturn(true);
        Mockito.when(channelRepo.findByTypeAndReferenceId(eq(ChatChannelType.FRIENDS), eq(expectedReferenceId)))
                .thenReturn(Optional.empty());

        Mockito.doAnswer(inv -> null).when(channelRepo).save(any());

        ChatMessage result = service.sendMessage(ChatChannelType.FRIENDS, friendId, userId, "Hello friend!", jwt);

        assertEquals(userId, result.senderId());
        assertEquals("Hello friend!", result.content());
        Mockito.verify(botRepo, Mockito.never()).askBot(any(), any(), any(), any());
    }

    @Test
    void friendsMessage_publishesRabbitMQEvent() {
        String friendId = "550e8400-e29b-41d4-a716-446655440000";
        String userId = "660e8400-e29b-41d4-a716-446655440000";
        String expectedReferenceId = friendId + "-" + userId;

        Mockito.when(socialRepo.areFriends(userId, friendId, "jwt-token")).thenReturn(true);
        Mockito.when(channelRepo.findByTypeAndReferenceId(eq(ChatChannelType.FRIENDS), eq(expectedReferenceId)))
                .thenReturn(Optional.empty());

        service.sendMessage(ChatChannelType.FRIENDS, friendId, userId, "Hello!", jwt);

        Mockito.verify(chatEventPublisher).publishDirectMessage(
                eq(UUID.fromString(userId)),
                eq("testuser"),
                eq(UUID.fromString(friendId)),
                eq("Hello!"),
                any(UUID.class)
        );
    }

    @Test
    void friendsMessage_existingChannelUsed() {
        String friendId = "550e8400-e29b-41d4-a716-446655440000";
        String userId = "660e8400-e29b-41d4-a716-446655440000";
        String expectedReferenceId = friendId + "-" + userId;

        Channel channel = Channel.createNew(ChatChannelType.FRIENDS, expectedReferenceId);

        Mockito.when(socialRepo.areFriends(userId, friendId, "jwt-token")).thenReturn(true);
        Mockito.when(channelRepo.findByTypeAndReferenceId(eq(ChatChannelType.FRIENDS), eq(expectedReferenceId)))
                .thenReturn(Optional.of(channel));

        ChatMessage result = service.sendMessage(ChatChannelType.FRIENDS, friendId, userId, "Hi!", jwt);

        assertEquals(userId, result.senderId());
        assertEquals("Hi!", result.content());
    }

    @Test
    void getMessages_friends_notFriends_throws() {
        String friendId = "550e8400-e29b-41d4-a716-446655440000";
        Mockito.when(socialRepo.areFriends("user1", friendId, "jwt-token"))
                .thenReturn(false);

        assertThrows(NotFriendsException.class, () ->
                service.getMessages(ChatChannelType.FRIENDS, friendId, null, "user1", jwt)
        );
    }

    @Test
    void getMessages_friends_areFriends_returnsMessages() {
        String friendId = "550e8400-e29b-41d4-a716-446655440000";
        String userId = "660e8400-e29b-41d4-a716-446655440000";
        String expectedReferenceId = friendId + "-" + userId;

        Channel channel = Channel.createNew(ChatChannelType.FRIENDS, expectedReferenceId);
        channel.postUserMessage(userId, "msg1");

        Mockito.when(socialRepo.areFriends(userId, friendId, "jwt-token")).thenReturn(true);
        Mockito.when(channelRepo.findByTypeAndReferenceId(ChatChannelType.FRIENDS, expectedReferenceId))
                .thenReturn(Optional.of(channel));

        List<ChatMessage> result = service.getMessages(ChatChannelType.FRIENDS, friendId, null, userId, jwt);

        assertEquals(1, result.size());
        assertEquals("msg1", result.getFirst().content());
    }

    @Test
    void friendsReferenceId_isSorted_userFirst() {
        String friendId = "ff0e8400-e29b-41d4-a716-446655440000";
        String userId = "aa0e8400-e29b-41d4-a716-446655440000";
        String expectedReferenceId = userId + "-" + friendId;

        Mockito.when(socialRepo.areFriends(userId, friendId, "jwt-token")).thenReturn(true);
        Mockito.when(channelRepo.findByTypeAndReferenceId(eq(ChatChannelType.FRIENDS), eq(expectedReferenceId)))
                .thenReturn(Optional.empty());

        service.sendMessage(ChatChannelType.FRIENDS, friendId, userId, "test", jwt);

        Mockito.verify(channelRepo).findByTypeAndReferenceId(ChatChannelType.FRIENDS, expectedReferenceId);
    }

    @Test
    void friendsReferenceId_isSorted_friendFirst() {
        String friendId = "aa0e8400-e29b-41d4-a716-446655440000";
        String userId = "ff0e8400-e29b-41d4-a716-446655440000";
        String expectedReferenceId = friendId + "-" + userId;

        Mockito.when(socialRepo.areFriends(userId, friendId, "jwt-token")).thenReturn(true);
        Mockito.when(channelRepo.findByTypeAndReferenceId(eq(ChatChannelType.FRIENDS), eq(expectedReferenceId)))
                .thenReturn(Optional.empty());

        service.sendMessage(ChatChannelType.FRIENDS, friendId, userId, "test", jwt);

        Mockito.verify(channelRepo).findByTypeAndReferenceId(ChatChannelType.FRIENDS, expectedReferenceId);
    }

    private ChatMessage injectMessage(Channel channel, ChatMessage message) throws Exception {
        var field = Channel.class.getDeclaredField("messages");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<ChatMessage> list = (List<ChatMessage>) field.get(channel);
        list.add(message);
        return message;
    }
}