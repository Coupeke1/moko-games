package be.kdg.team22.communicationservice.infrastructure.chat.jpa;

import be.kdg.team22.communicationservice.domain.chat.Channel;
import be.kdg.team22.communicationservice.domain.chat.ChatChannelId;
import be.kdg.team22.communicationservice.domain.chat.ChatChannelType;
import be.kdg.team22.communicationservice.domain.chat.ChatMessage;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChannelEntityTest {

    @Test
    void fromDomain_convertsChannelAndMessagesCorrectly() {
        ChatChannelId channelId = ChatChannelId.create();
        Channel channel = Channel.createNew(ChatChannelType.BOT, "user123"); // AI mag alléén bij BOT

        ChatMessage m1 = channel.postUserMessage("user1", "hello");
        ChatMessage m2 = channel.postBotMessage("model1", "world");

        ChannelEntity entity = ChannelEntity.from(channel);

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
        ChannelEntity entity = new ChannelEntity(id, ChatChannelType.BOT, "refA");

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

        Channel domain = entity.to();

        assertEquals(id, domain.getId().value());
        assertEquals(ChatChannelType.BOT, domain.getType());
        assertEquals("refA", domain.getReferenceId());
        assertEquals(2, domain.getMessages().size());

        assertEquals("first", domain.getMessages().get(0).content());
        assertEquals("second", domain.getMessages().get(1).content());
    }

    @Test
    void roundTrip_preservesAllFields() {
        Channel channel = Channel.createNew(ChatChannelType.BOT, "ref55");
        channel.postUserMessage("u1", "msg1");
        channel.postBotMessage("modelX", "msg2");

        ChannelEntity entity = ChannelEntity.from(channel);
        Channel converted = entity.to();

        assertEquals(channel.getId().value(), converted.getId().value());
        assertEquals(channel.getType(), converted.getType());
        assertEquals(channel.getReferenceId(), converted.getReferenceId());

        assertEquals(2, converted.getMessages().size());
        assertEquals("msg1", converted.getMessages().get(0).content());
        assertEquals("msg2", converted.getMessages().get(1).content());
    }

    @Test
    void fromDomain_emptyMessagesProducesEmptyEntityList() {
        Channel channel = Channel.createNew(ChatChannelType.BOT, "ref-empty");

        ChannelEntity entity = ChannelEntity.from(channel);

        assertEquals(0, entity.getMessages().size());
    }

    @Test
    void orphanRemovalMapping_removesMessageWhenRemovedFromList() {
        ChannelEntity entity =
                new ChannelEntity(UUID.randomUUID(), ChatChannelType.LOBBY, "l123");

        ChatMessageEntity msg = new ChatMessageEntity(
                UUID.randomUUID(), entity, "u", "x", Instant.now()
        );

        entity.getMessages().add(msg);
        assertEquals(1, entity.getMessages().size());

        entity.getMessages().remove(msg);
        assertEquals(0, entity.getMessages().size());
    }
}
