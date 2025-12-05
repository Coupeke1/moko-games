package be.kdg.team22.communicationservice.infrastructure.chat.jpa;

import be.kdg.team22.communicationservice.domain.chat.ChatChannel;
import be.kdg.team22.communicationservice.domain.chat.ChatChannelId;
import be.kdg.team22.communicationservice.domain.chat.ChatChannelType;
import be.kdg.team22.communicationservice.domain.chat.ChatMessage;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatChannelEntityTest {

    @Test
    void fromDomain_convertsChannelAndMessagesCorrectly() {
        ChatChannelId channelId = ChatChannelId.create();
        ChatChannel channel = ChatChannel.createNew(ChatChannelType.BOT, "user123"); // AI mag alléén bij BOT

        ChatMessage m1 = channel.postUserMessage("user1", "hello");
        ChatMessage m2 = channel.postBotMessage("model1", "world");

        ChatChannelEntity entity = ChatChannelEntity.from(channel);

        assertEquals(channel.getId().value(), entity.getId());
        assertEquals(ChatChannelType.BOT, entity.getType());
        assertEquals("user123", entity.getReferenceId());

        assertEquals(2, entity.getMessages().size());
        assertEquals("hello", entity.getMessages().get(0).getContent());
        assertEquals("world", entity.getMessages().get(1).getContent());
    }

    @Test
    void toDomain_convertsEntityBackToDomainCorrectly() {
        UUID id = UUID.randomUUID();
        ChatChannelEntity entity = new ChatChannelEntity(id, ChatChannelType.BOT, "refA");

        ChatMessageEntity m1 = new ChatMessageEntity(
                UUID.randomUUID(),
                entity,
                "user123",
                "first",
                Instant.parse("2025-01-01T00:00:00Z")
        );

        ChatMessageEntity m2 = new ChatMessageEntity(
                UUID.randomUUID(),
                entity,
                "bot:xyz",
                "second",
                Instant.parse("2025-01-01T00:00:05Z")
        );

        entity.getMessages().add(m1);
        entity.getMessages().add(m2);

        ChatChannel domain = entity.to();

        assertEquals(id, domain.getId().value());
        assertEquals(ChatChannelType.BOT, domain.getType());
        assertEquals("refA", domain.getReferenceId());
        assertEquals(2, domain.getMessages().size());

        assertEquals("first", domain.getMessages().get(0).content());
        assertEquals("second", domain.getMessages().get(1).content());
    }

    @Test
    void roundTrip_preservesAllFields() {
        ChatChannel channel = ChatChannel.createNew(ChatChannelType.BOT, "ref55");
        channel.postUserMessage("u1", "msg1");
        channel.postBotMessage("modelX", "msg2");

        ChatChannelEntity entity = ChatChannelEntity.from(channel);
        ChatChannel converted = entity.to();

        assertEquals(channel.getId().value(), converted.getId().value());
        assertEquals(channel.getType(), converted.getType());
        assertEquals(channel.getReferenceId(), converted.getReferenceId());

        assertEquals(2, converted.getMessages().size());
        assertEquals("msg1", converted.getMessages().get(0).content());
        assertEquals("msg2", converted.getMessages().get(1).content());
    }

    @Test
    void fromDomain_emptyMessagesProducesEmptyEntityList() {
        ChatChannel channel = ChatChannel.createNew(ChatChannelType.BOT, "ref-empty");

        ChatChannelEntity entity = ChatChannelEntity.from(channel);

        assertEquals(0, entity.getMessages().size());
    }

    @Test
    void orphanRemovalMapping_removesMessageWhenRemovedFromList() {
        ChatChannelEntity entity =
                new ChatChannelEntity(UUID.randomUUID(), ChatChannelType.LOBBY, "l123");

        ChatMessageEntity msg = new ChatMessageEntity(
                UUID.randomUUID(), entity, "u", "x", Instant.now()
        );

        entity.getMessages().add(msg);
        assertEquals(1, entity.getMessages().size());

        entity.getMessages().remove(msg);
        assertEquals(0, entity.getMessages().size());
    }
}
