package be.kdg.team22.tictactoeservice.domain.events;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameEndedEventTest {

    @Test
    void shouldCreateGameEndedEvent() {
        UUID instanceId = UUID.randomUUID();
        Instant occurredAt = Instant.now();

        GameEndedEvent event = new GameEndedEvent(instanceId, occurredAt);

        assertEquals(instanceId, event.instanceId());
        assertEquals(occurredAt, event.occurredAt());
    }

    @Test
    void shouldHaveCorrectEquality() {
        UUID instanceId = UUID.randomUUID();
        Instant occurredAt = Instant.now();

        GameEndedEvent event1 = new GameEndedEvent(instanceId, occurredAt);
        GameEndedEvent event2 = new GameEndedEvent(instanceId, occurredAt);

        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    void shouldNotBeEqualWithDifferentInstanceId() {
        Instant occurredAt = Instant.now();

        GameEndedEvent event1 = new GameEndedEvent(UUID.randomUUID(), occurredAt);
        GameEndedEvent event2 = new GameEndedEvent(UUID.randomUUID(), occurredAt);

        assertNotEquals(event1, event2);
    }
}

