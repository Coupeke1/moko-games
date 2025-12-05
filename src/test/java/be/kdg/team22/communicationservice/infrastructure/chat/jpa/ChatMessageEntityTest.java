package be.kdg.team22.communicationservice.infrastructure.chat.jpa;

import be.kdg.team22.communicationservice.domain.chat.ChatChannelId;
import be.kdg.team22.communicationservice.domain.chat.ChatChannelType;
import be.kdg.team22.communicationservice.domain.chat.ChatMessage;
import be.kdg.team22.communicationservice.domain.chat.ChatMessageId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatMessageEntityTest {

    @Test
    void toDomain_convertsEntityCorrectly() {
        UUID channelId = UUID.randomUUID();
        UUID msgId = UUID.randomUUID();

        ChatChannelEntity parent = new ChatChannelEntity(channelId, ChatChannelType.LOBBY, "lobby1");
        ChatMessageEntity entity = new ChatMessageEntity(
                msgId,
                parent,
                "user123",
                "Hello!",
                Instant.parse("2025-01-01T10:00:00Z")
        );

        ChatMessage domain = entity.to();

        assertEquals(msgId, domain.id().value());
        assertEquals(channelId, domain.channelId().value());
        assertEquals("user123", domain.senderId());
        assertEquals("Hello!", domain.content());
        assertEquals(Instant.parse("2025-01-01T10:00:00Z"), domain.timestamp());
    }

    @Test
    void fromDomain_createsEntityCorrectly() {
        ChatChannelId chId = ChatChannelId.create();
        ChatMessage domain = new ChatMessage(
                ChatMessageId.create(),
                chId,
                "user1",
                "Test message",
                Instant.parse("2025-02-02T12:00:00Z")
        );

        ChatChannelEntity parent = new ChatChannelEntity(chId.value(),
                ChatChannelType.BOT,
                "user1");

        ChatMessageEntity entity = ChatMessageEntity.from(domain, parent);

        assertEquals(domain.id().value(), entity.getId());
        assertEquals(parent, entity.getChannel());
        assertEquals("user1", entity.getSenderId());
        assertEquals("Test message", entity.getContent());
        assertEquals(Instant.parse("2025-02-02T12:00:00Z"), entity.getTimestamp());
    }

    @Test
    void roundTrip_mappingPreservesData() {
        ChatChannelId chId = ChatChannelId.create();
        ChatMessage original = new ChatMessage(
                ChatMessageId.create(),
                chId,
                "sender",
                "roundtrip",
                Instant.parse("2030-03-03T15:00:00Z")
        );

        ChatChannelEntity parent = new ChatChannelEntity(
                chId.value(),
                ChatChannelType.LOBBY,
                "ref123"
        );

        ChatMessageEntity entity = ChatMessageEntity.from(original, parent);
        ChatMessage converted = entity.to();

        assertEquals(original.id().value(), converted.id().value());
        assertEquals(original.content(), converted.content());
        assertEquals(original.senderId(), converted.senderId());
        assertEquals(original.timestamp(), converted.timestamp());
    }
}