package be.kdg.team22.gameaclservice.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Game")
class GameTest {

    @Test
    @DisplayName("should create game with random id")
    void testCreateGameWithRandomId() {
        List<UUID> players = List.of(UUID.randomUUID(), UUID.randomUUID());

        Game game = Game.create(players);

        assertThat(game.id()).isNotNull();
    }

    @Test
    @DisplayName("should create game with provided players")
    void testCreateGameWithProvidedPlayers() {
        UUID player1 = UUID.randomUUID();
        UUID player2 = UUID.randomUUID();
        List<UUID> players = List.of(player1, player2);

        Game game = Game.create(players);

        assertThat(game.players()).containsExactlyElementsOf(players);
    }

    @Test
    @DisplayName("should generate unique ids for multiple games")
    void testCreateMultipleGamesGenerateUniqueIds() {
        List<UUID> players = List.of(UUID.randomUUID(), UUID.randomUUID());

        Game game1 = Game.create(players);
        Game game2 = Game.create(players);

        assertThat(game1.id()).isNotEqualTo(game2.id());
    }

    @Test
    @DisplayName("should allow id setter")
    void testSetGameId() {
        List<UUID> players = List.of(UUID.randomUUID());
        Game game = Game.create(players);
        UUID newId = UUID.randomUUID();

        game.setId(newId);

        assertThat(game.id()).isEqualTo(newId);
    }

    @Test
    @DisplayName("should preserve player order")
    void testGamePreservesPlayerOrder() {
        UUID player1 = UUID.randomUUID();
        UUID player2 = UUID.randomUUID();
        UUID player3 = UUID.randomUUID();
        List<UUID> players = List.of(player1, player2, player3);

        Game game = Game.create(players);

        assertThat(game.players())
                .containsExactly(player1, player2, player3);
    }

    @Test
    @DisplayName("should support single player game")
    void testGameWithSinglePlayer() {
        UUID player = UUID.randomUUID();
        List<UUID> players = List.of(player);

        Game game = Game.create(players);

        assertThat(game.players()).containsExactly(player);
    }

    @Test
    @DisplayName("should support multiple players")
    void testGameWithMultiplePlayers() {
        List<UUID> players = List.of(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        Game game = Game.create(players);

        assertThat(game.players()).hasSize(4);
    }

    @Test
    @DisplayName("should allow id getter")
    void testGetGameId() {
        UUID player = UUID.randomUUID();
        Game game = Game.create(List.of(player));

        UUID id = game.id();

        assertThat(id).isNotNull();
        assertThat(id).isEqualTo(game.id());
    }

    @Test
    @DisplayName("should allow players getter")
    void testGetGamePlayers() {
        UUID player1 = UUID.randomUUID();
        UUID player2 = UUID.randomUUID();
        List<UUID> players = List.of(player1, player2);

        Game game = Game.create(players);

        assertThat(game.players()).isEqualTo(players);
    }

    @Test
    @DisplayName("should support empty player list")
    void testGameWithEmptyPlayerList() {
        Game game = Game.create(List.of());

        assertThat(game.players()).isEmpty();
    }
}
