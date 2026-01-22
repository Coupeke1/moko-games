package be.kdg.team22.userservice.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GamesPurchasedEventTest {

    @Test
    @DisplayName("GamesPurchasedEvent → stores userId and gameIds correctly")
    void gamesPurchasedEvent_storesData() {
        UUID userId = UUID.randomUUID();
        UUID gameId1 = UUID.randomUUID();
        UUID gameId2 = UUID.randomUUID();
        List<UUID> gameIds = List.of(gameId1, gameId2);

        GamesPurchasedEvent event = new GamesPurchasedEvent(userId, gameIds);

        assertThat(event.userId()).isEqualTo(userId);
        assertThat(event.gameIds()).containsExactly(gameId1, gameId2);
    }

    @Test
    @DisplayName("GamesPurchasedEvent → equals and hashCode work correctly")
    void gamesPurchasedEvent_equalsAndHashCode() {
        UUID userId = UUID.randomUUID();
        List<UUID> gameIds = List.of(UUID.randomUUID());

        GamesPurchasedEvent event1 = new GamesPurchasedEvent(userId, gameIds);
        GamesPurchasedEvent event2 = new GamesPurchasedEvent(userId, gameIds);

        assertThat(event1).isEqualTo(event2);
        assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
    }

    @Test
    @DisplayName("GamesPurchasedEvent → toString contains relevant info")
    void gamesPurchasedEvent_toString() {
        UUID userId = UUID.randomUUID();
        List<UUID> gameIds = List.of(UUID.randomUUID());

        GamesPurchasedEvent event = new GamesPurchasedEvent(userId, gameIds);

        assertThat(event.toString()).contains(userId.toString());
    }
}