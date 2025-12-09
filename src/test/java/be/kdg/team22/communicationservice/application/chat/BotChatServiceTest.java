package be.kdg.team22.communicationservice.application.chat;

import be.kdg.team22.communicationservice.domain.chat.*;
import be.kdg.team22.communicationservice.domain.chat.bot.BotChatRepository;
import be.kdg.team22.communicationservice.domain.chat.bot.BotResponse;
import be.kdg.team22.communicationservice.domain.chat.exceptions.NotBotChannelOwnerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class BotChatServiceTest {

    ChatChannelRepository channelRepo;
    BotChatRepository botRepo;
    BotChatService service;

    @BeforeEach
    void setup() {
        channelRepo = Mockito.mock(ChatChannelRepository.class);
        botRepo = Mockito.mock(BotChatRepository.class);
        service = new BotChatService(channelRepo, botRepo);
    }

    @Test
    void createChannel_createsNewChannel() {
        String userId = "user123";

        Mockito.when(channelRepo.findByTypeAndReferenceId(eq(ChatChannelType.BOT), eq(userId)))
                .thenReturn(Optional.empty());

        Channel channel = service.createChannel(userId);

        assertEquals(ChatChannelType.BOT, channel.getType());
        assertEquals(userId, channel.getReferenceId());
        Mockito.verify(channelRepo).save(any());
    }

    @Test
    void createChannel_returnsExistingChannel() {
        String userId = "user123";
        Channel existing = Channel.createNew(ChatChannelType.BOT, userId);

        Mockito.when(channelRepo.findByTypeAndReferenceId(eq(ChatChannelType.BOT), eq(userId)))
                .thenReturn(Optional.of(existing));

        Channel channel = service.createChannel(userId);

        assertSame(existing, channel);
        Mockito.verify(channelRepo, Mockito.never()).save(any());
    }

    @Test
    void sendMessage_returnsAIMessage() {
        String userId = "user123";
        Channel channel = Channel.createNew(ChatChannelType.BOT, userId);
        UUID channelId = channel.getId().value();

        Mockito.when(channelRepo.findById(eq(ChatChannelId.from(channelId))))
                .thenReturn(Optional.of(channel));

        BotResponse botResp = new BotResponse("Hello human");
        Mockito.when(botRepo.askBot("Hi bot", "GO")).thenReturn(botResp);

        ChatMessage msg = service.sendMessage(channelId, userId, "Hi bot", "GO");

        assertEquals("bot:bot", msg.senderId());
        assertEquals("Hello human", msg.content());
        Mockito.verify(botRepo).askBot("Hi bot", "GO");
    }

    @Test
    void sendMessage_notOwner_throws() {
        String ownerId = "user123";
        String otherUserId = "user456";
        Channel channel = Channel.createNew(ChatChannelType.BOT, ownerId);
        UUID channelId = channel.getId().value();

        Mockito.when(channelRepo.findById(eq(ChatChannelId.from(channelId))))
                .thenReturn(Optional.of(channel));

        assertThrows(NotBotChannelOwnerException.class, () ->
                service.sendMessage(channelId, otherUserId, "Hi bot", "GO")
        );
    }

    @Test
    void getMessages_returnsMessages() {
        String userId = "user123";
        Channel channel = Channel.createNew(ChatChannelType.BOT, userId);
        channel.postUserMessage(userId, "Hello");
        UUID channelId = channel.getId().value();

        Mockito.when(channelRepo.findById(eq(ChatChannelId.from(channelId))))
                .thenReturn(Optional.of(channel));

        List<ChatMessage> messages = service.getMessages(channelId, userId, null);

        assertEquals(1, messages.size());
        assertEquals("Hello", messages.getFirst().content());
    }

    @Test
    void getMessages_notOwner_throws() {
        String ownerId = "user123";
        String otherUserId = "user456";
        Channel channel = Channel.createNew(ChatChannelType.BOT, ownerId);
        UUID channelId = channel.getId().value();

        Mockito.when(channelRepo.findById(eq(ChatChannelId.from(channelId))))
                .thenReturn(Optional.of(channel));

        assertThrows(NotBotChannelOwnerException.class, () ->
                service.getMessages(channelId, otherUserId, null)
        );
    }
}